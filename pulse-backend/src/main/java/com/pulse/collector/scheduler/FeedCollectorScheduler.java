package com.pulse.collector.scheduler;

import com.pulse.collector.service.CollectorService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class FeedCollectorScheduler {

    private static final Logger log = LoggerFactory.getLogger(FeedCollectorScheduler.class);

    @Inject
    CollectorService collectorService;

    // Run every 30 minutes, with 30s startup delay
    @Scheduled(every = "30m", delayed = "30s")
    public void collectFeeds() {
        log.info("=== Feed collection triggered ===");
        collectorService.collectAll();
        log.info("=== Feed collection complete ===");
    }
}
