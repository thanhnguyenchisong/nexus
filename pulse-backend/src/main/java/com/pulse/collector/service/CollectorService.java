package com.pulse.collector.service;

import com.pulse.article.entity.Article;
import com.pulse.article.entity.Source;
import com.pulse.article.repository.ArticleRepository;
import com.pulse.article.repository.SourceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class CollectorService {

    private static final Logger log = LoggerFactory.getLogger(CollectorService.class);
    private static final Pattern IMG_PATTERN = Pattern.compile("<img[^>]+src=['\"]([^'\"]+)['\"]", Pattern.CASE_INSENSITIVE);

    @Inject
    SourceRepository sourceRepository;

    @Inject
    ArticleRepository articleRepository;

    public void collectAll() {
        List<Source> sources = sourceRepository.find("status", "ACTIVE").list();
        log.info("Starting collection for {} active sources", sources.size());
        for (Source source : sources) {
            try {
                collectSource(source);
            } catch (Exception e) {
                log.error("Failed collecting source [{}]: {}", source.name, e.getMessage());
                markSourceError(source);
            }
        }
    }

    @Transactional
    public void collectSource(Source source) {
        log.info("Fetching: {} -> {}", source.name, source.feedUrl);
        try {
            // Re-attach source in the current transaction context
            Source attachedSource = sourceRepository.findById(source.id);
            if (attachedSource == null) return;

            // Fetch and parse RSS feed using Rome
            URL feedSource = new URL(attachedSource.feedUrl);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));
            
            List<SyndEntry> entries = feed.getEntries();
            if (entries == null) {
                log.warn("No entries found for source: {}", attachedSource.name);
                return;
            }

            int saved = 0;
            for (SyndEntry entry : entries) {
                if (saveArticle(entry, attachedSource)) saved++;
            }

            log.info("Saved {}/{} new articles from {}", saved, entries.size(), attachedSource.name);
            updateSourceFetched(attachedSource);
        } catch (Exception e) {
            log.error("RSS fetch error [{}]: {}", source.name, e.getMessage());
            markSourceError(source);
        }
    }

    private boolean saveArticle(SyndEntry entry, Source source) {
        String url = entry.getLink();
        if (url == null || url.isBlank()) return false;

        // Dedup by URL
        if (articleRepository.findByOriginalUrl(url) != null) return false;

        Article article = new Article();
        article.source = source;
        article.domain = source.domain;
        article.language = source.language;
        article.originalUrl = url;

        // Title
        article.title = entry.getTitle() != null
                ? entry.getTitle().substring(0, Math.min(entry.getTitle().length(), 500))
                : "(no title)";

        // Description — strip HTML
        String rawDesc = null;
        if (entry.getDescription() != null) {
            rawDesc = entry.getDescription().getValue();
        } else if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            rawDesc = entry.getContents().get(0).getValue();
        }
        if (rawDesc != null) {
            // Extract thumbnail from HTML if no media attachment
            String thumb = extractImgFromHtml(rawDesc);
            if (thumb != null && article.thumbnailUrl == null) {
                article.thumbnailUrl = thumb;
            }
            // Strip HTML tags and truncate
            String cleaned = rawDesc.replaceAll("<[^>]+>", "").replaceAll("\\s+", " ").trim();
            article.description = cleaned.substring(0, Math.min(cleaned.length(), 500));
        }

        // Author
        if (entry.getAuthors() != null && !entry.getAuthors().isEmpty()) {
            article.author = entry.getAuthors().get(0).getName();
        }

        // Thumbnail from media:content
        if (entry.getEnclosures() != null) {
            for (SyndEnclosure enc : entry.getEnclosures()) {
                if (enc.getType() != null && enc.getType().startsWith("image/")) {
                    article.thumbnailUrl = enc.getUrl();
                    break;
                }
            }
        }
        // Try media:content via foreign markup
        if (article.thumbnailUrl == null && entry.getForeignMarkup() != null) {
            entry.getForeignMarkup().forEach(elem -> {
                if ("content".equals(elem.getName()) && "media".equals(elem.getNamespacePrefix())) {
                    String mediaUrl = elem.getAttributeValue("url");
                    if (mediaUrl != null && article.thumbnailUrl == null) {
                        article.thumbnailUrl = mediaUrl;
                    }
                }
            });
        }

        // Published date
        Date pubDate = entry.getPublishedDate() != null ? entry.getPublishedDate() : entry.getUpdatedDate();
        if (pubDate != null) {
            article.publishedAt = pubDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            article.publishedAt = LocalDateTime.now();
        }

        article.createdAt = LocalDateTime.now();

        try {
            articleRepository.persist(article);
            return true;
        } catch (Exception e) {
            // Unique constraint violation (race condition) — not an error
            log.debug("Article already exists (race): {}", url);
            return false;
        }
    }

    private String extractImgFromHtml(String html) {
        if (html == null) return null;
        Matcher m = IMG_PATTERN.matcher(html);
        return m.find() ? m.group(1) : null;
    }

    @Transactional
    protected void updateSourceFetched(Source source) {
        Source attached = sourceRepository.findById(source.id);
        if (attached != null) {
            attached.lastFetchedAt = LocalDateTime.now();
            attached.errorCount = 0;
        }
    }

    @Transactional
    protected void markSourceError(Source source) {
        Source attached = sourceRepository.findById(source.id);
        if (attached != null) {
            attached.errorCount++;
            if (attached.errorCount >= 5) {
                attached.status = "ERROR";
            }
        }
    }
}
