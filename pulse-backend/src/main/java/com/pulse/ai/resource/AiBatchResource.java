package com.pulse.ai.resource;

import com.pulse.ai.service.AiSummaryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/ai")
@Produces(MediaType.APPLICATION_JSON)
public class AiBatchResource {

    @Inject
    AiSummaryService aiSummaryService;

    /**
     * POST /api/v1/ai/batch
     * Manually trigger AI batch processing (useful in dev).
     */
    @POST
    @Path("/batch")
    public Response triggerBatch() {
        aiSummaryService.processBatch();
        return Response.ok("{\"message\": \"AI batch triggered\"}").build();
    }
}
