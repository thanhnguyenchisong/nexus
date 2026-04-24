package com.pulse.ai.repository;

import com.pulse.ai.entity.AiSummary;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AiSummaryRepository implements PanacheRepository<AiSummary> {

    public Optional<AiSummary> findByArticleId(Long articleId) {
        return find("article.id", articleId).firstResultOptional();
    }

    public boolean existsForArticle(Long articleId) {
        return count("article.id", articleId) > 0;
    }

    /** Articles that have no summary yet — for batch processing */
    public java.util.List<Long> findUnsummarizedArticleIds(int limit) {
        return getEntityManager()
                .createQuery(
                    "select a.id from Article a where a.id not in " +
                    "(select s.article.id from AiSummary s) " +
                    "and length(coalesce(a.description,'')) >= 100 " +
                    "order by a.publishedAt desc",
                    Long.class)
                .setMaxResults(limit)
                .getResultList();
    }
}
