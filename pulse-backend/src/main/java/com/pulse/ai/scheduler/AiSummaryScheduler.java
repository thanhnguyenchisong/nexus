package com.pulse.ai.scheduler;

import com.pulse.ai.service.AiSummaryService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AiSummaryScheduler {

    private static final Logger log = LoggerFactory.getLogger(AiSummaryScheduler.class);

    @Inject
    AiSummaryService aiSummaryService;

    // Run every 2 hours, 5-minute startup delay to let app settle
    @Scheduled(every = "2h", delayed = "5m")
    public void runBatch() {
        log.info("=== AI summary batch starting ===");
        aiSummaryService.processBatch();
        log.info("=== AI summary batch complete ===");
    }
}
