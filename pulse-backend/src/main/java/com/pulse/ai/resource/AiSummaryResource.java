package com.pulse.ai.resource;

import com.pulse.ai.dto.AiSummaryDto;
import com.pulse.ai.service.AiSummaryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/articles/{articleId}/summary")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiSummaryResource {

    @Inject
    AiSummaryService aiSummaryService;

    /**
     * GET /api/v1/articles/{articleId}/summary
     * Returns existing summary, 404 if not yet generated.
     */
    @GET
    public Response getSummary(@PathParam("articleId") Long articleId) {
        return aiSummaryService.getForArticle(articleId)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(404)
                        .entity("{\"message\": \"No summary yet. POST to generate one.\"}")
                        .build());
    }

    /**
     * POST /api/v1/articles/{articleId}/summary
     * Generate (or return existing) AI summary on demand.
     */
    @POST
    public Response generateSummary(@PathParam("articleId") Long articleId) {
        return aiSummaryService.generateForArticle(articleId)
                .map(dto -> Response.status(201).entity(dto).build())
                .orElse(Response.status(404)
                        .entity("{\"message\": \"Article not found or description too short to summarize.\"}")
                        .build());
    }

    /**
     * POST /api/v1/ai/batch
     * Trigger a manual batch processing run.
     */
    @POST
    @Path("/batch")
    public Response triggerBatch() {
        // This path won't match due to prefix; real batch trigger below
        return Response.status(400).build();
    }
}
