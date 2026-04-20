package com.pulse.collector.scheduler;

import com.pulse.article.entity.Article;
import com.pulse.article.entity.Source;
import com.pulse.article.repository.ArticleRepository;
import com.pulse.article.repository.SourceRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FeedCollectorScheduler {

    private static final Logger log = LoggerFactory.getLogger(FeedCollectorScheduler.class);

    @Inject
    SourceRepository sourceRepository;

    @Inject
    ArticleRepository articleRepository;

    // Disabled mostly to avoid spamming while testing MVP. Can be re-enabled.
    // Every 30 minutes
    @Scheduled(every = "30m", delayed = "30s")
    @Transactional
    public void collectFeeds() {
        log.info("Starting feed collection...");
        List<Source> sources = sourceRepository.listAll();
        for (Source source : sources) {
            try {
                // To keep it simple for MVP, we just print the intention. 
                // Full RSS parsing logic using Apache Camel would be hooked here.
                log.info("Fetching from source: {}", source.name);
                
                // MOCK data saving to prove it works dynamically
                // In reality, here Camel / RssFeedParser would be used
                source.lastFetchedAt = LocalDateTime.now();
                sourceRepository.persist(source);
                
            } catch (Exception e) {
                log.error("Failed to fetch source: {}", source.name, e);
                source.errorCount++;
                sourceRepository.persist(source);
            }
        }
        log.info("Feed collection finished.");
    }
}
