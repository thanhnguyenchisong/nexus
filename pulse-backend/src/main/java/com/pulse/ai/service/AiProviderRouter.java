package com.pulse.ai.service;

import com.pulse.ai.client.AiProviderException;
import com.pulse.ai.client.ClaudeClient;
import com.pulse.ai.client.OpenAiClient;
import com.pulse.ai.model.AiAnalysisResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes AI requests to OpenAI first, falls back to Claude on failure.
 */
@ApplicationScoped
public class AiProviderRouter {

    private static final Logger log = LoggerFactory.getLogger(AiProviderRouter.class);

    @Inject
    OpenAiClient openAiClient;

    @Inject
    ClaudeClient claudeClient;

    /**
     * Try OpenAI first. If it fails, try Claude.
     * Throws AiProviderException only if BOTH fail.
     */
    public AiAnalysisResult analyze(String prompt) {
        // Primary: OpenAI
        try {
            AiAnalysisResult result = openAiClient.analyze(prompt);
            log.debug("AI analysis completed via OpenAI");
            return result;
        } catch (AiProviderException e) {
            log.warn("OpenAI failed ({}), falling back to Claude", e.getMessage());
        }

        // Fallback: Claude
        try {
            AiAnalysisResult result = claudeClient.analyze(prompt);
            log.debug("AI analysis completed via Claude (fallback)");
            return result;
        } catch (AiProviderException e) {
            log.error("Both AI providers failed: {}", e.getMessage());
            throw new AiProviderException("All AI providers exhausted", e);
        }
    }
}
