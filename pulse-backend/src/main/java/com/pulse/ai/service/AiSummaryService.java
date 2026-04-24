package com.pulse.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.ai.client.AiProviderException;
import com.pulse.ai.dto.AiSummaryDto;
import com.pulse.ai.entity.AiSummary;
import com.pulse.ai.model.AiAnalysisResult;
import com.pulse.ai.prompt.PromptTemplates;
import com.pulse.ai.repository.AiSummaryRepository;
import com.pulse.article.entity.Article;
import com.pulse.article.repository.ArticleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class AiSummaryService {

    private static final Logger log = LoggerFactory.getLogger(AiSummaryService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Estimated cost guard: ~$0.002 per article for gpt-4o-mini
    private static final int BATCH_SIZE = 10;
    private static final int MIN_DESCRIPTION_LENGTH = 100;

    @Inject
    AiSummaryRepository summaryRepository;

    @Inject
    ArticleRepository articleRepository;

    @Inject
    AiProviderRouter router;

    @Inject
    PromptTemplates prompts;

    @ConfigProperty(name = "ai.monthly.article.cap", defaultValue = "500")
    int monthlyArticleCap;

    // In-memory counter for this JVM instance (reset on restart — fine for MVP)
    private final AtomicInteger processedThisMonth = new AtomicInteger(0);

    // ─── Public API ────────────────────────────────────────────────────────────

    /**
     * Generate summary for a single article. If one already exists, return it.
     * Called from REST endpoint POST /api/v1/articles/{id}/summary
     */
    @Transactional
    public Optional<AiSummaryDto> generateForArticle(Long articleId) {
        // Return existing if present
        Optional<AiSummary> existing = summaryRepository.findByArticleId(articleId);
        if (existing.isPresent()) {
            return Optional.of(AiSummaryDto.from(existing.get()));
        }

        Article article = articleRepository.findById(articleId);
        if (article == null) return Optional.empty();

        return Optional.ofNullable(processArticle(article))
                .map(AiSummaryDto::from);
    }

    /**
     * Get existing summary (no generation).
     * Called from REST endpoint GET /api/v1/articles/{id}/summary
     */
    public Optional<AiSummaryDto> getForArticle(Long articleId) {
        return summaryRepository.findByArticleId(articleId).map(AiSummaryDto::from);
    }

    /**
     * Batch process unsummarized articles.
     * Called by scheduler every 2 hours.
     */
    public void processBatch() {
        if (processedThisMonth.get() >= monthlyArticleCap) {
            log.warn("Monthly AI article cap ({}) reached — skipping batch", monthlyArticleCap);
            return;
        }

        List<Long> ids = summaryRepository.findUnsummarizedArticleIds(BATCH_SIZE);
        if (ids.isEmpty()) {
            log.info("No articles to summarize");
            return;
        }

        log.info("AI batch: processing {} articles", ids.size());
        int saved = 0;
        for (Long id : ids) {
            if (processedThisMonth.get() >= monthlyArticleCap) break;
            try {
                Article article = articleRepository.findById(id);
                if (article != null && processSingleInTransaction(article) != null) {
                    saved++;
                    processedThisMonth.incrementAndGet();
                }
            } catch (Exception e) {
                log.error("Failed to process article {}: {}", id, e.getMessage());
            }
        }
        log.info("AI batch complete: {}/{} summaries saved", saved, ids.size());
    }

    // ─── Private helpers ───────────────────────────────────────────────────────

    private AiSummary processArticle(Article article) {
        if (article.description == null || article.description.length() < MIN_DESCRIPTION_LENGTH) {
            log.debug("Article {} too short to summarize", article.id);
            return null;
        }
        return processSingleInTransaction(article);
    }

    @Transactional
    AiSummary processSingleInTransaction(Article article) {
        // Double-check dedup (race condition guard)
        if (summaryRepository.existsForArticle(article.id)) return null;

        String prompt = prompts.buildPrompt(article.title, article.description, article.language);

        AiAnalysisResult result;
        try {
            result = router.analyze(prompt);
        } catch (AiProviderException e) {
            log.error("All AI providers failed for article {}: {}", article.id, e.getMessage());
            return null;
        }

        if (result.summary() == null || result.summary().isBlank()) {
            log.warn("Empty summary returned for article {}", article.id);
            return null;
        }

        AiSummary summary = new AiSummary();
        summary.article = article;
        summary.summary = result.summary();
        summary.keyInsights = toJsonArray(result.keyInsights());
        summary.sentiment = result.sentiment();
        summary.sentimentScore = result.sentimentScore();
        summary.tags = toJsonArray(result.tags());
        summary.aiProvider = result.provider();
        summary.modelUsed = result.modelUsed();
        summary.generatedAt = LocalDateTime.now();

        summaryRepository.persist(summary);
        log.info("Saved AI summary for article {} ({})", article.id, result.provider());
        return summary;
    }

    private String toJsonArray(List<String> list) {
        try {
            return MAPPER.writeValueAsString(list != null ? list : List.of());
        } catch (Exception e) {
            return "[]";
        }
    }
}
