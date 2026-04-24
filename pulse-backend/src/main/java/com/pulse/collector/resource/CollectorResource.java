package com.pulse.collector.resource;

import com.pulse.collector.service.CollectorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/collector")
@Produces(MediaType.APPLICATION_JSON)
public class CollectorResource {

    @Inject
    CollectorService collectorService;

    /**
     * Trigger an immediate feed collection for all sources.
     * Useful during development / admin tasks.
     * POST /api/v1/collector/trigger
     */
    @POST
    @Path("/trigger")
    public Response triggerCollection() {
        collectorService.collectAll();
        return Response.ok("{\"message\": \"Collection triggered\"}").build();
    }
}
