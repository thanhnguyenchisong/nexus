package com.pulse.article.resource;

import com.pulse.article.dto.SourceDto;
import com.pulse.article.dto.SourceUpdateRequest;
import com.pulse.article.service.SourceService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/v1/sources")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SourceResource {

    @Inject
    SourceService sourceService;

    @GET
    public List<SourceDto> getSources(@QueryParam("domain") String domain) {
        if (domain != null && !domain.isBlank()) {
            return sourceService.getSourcesByDomain(domain);
        }
        return sourceService.getAllSources();
    }

    @GET
    @Path("/{id}")
    public Response getSource(@PathParam("id") Long id) {
        SourceDto dto = sourceService.getById(id);
        if (dto == null) return Response.status(404).build();
        return Response.ok(dto).build();
    }

    /**
     * PATCH /api/v1/sources/{id}
     * Toggle status (ACTIVE ↔ INACTIVE) or update fetch interval.
     */
    @PATCH
    @Path("/{id}")
    public Response updateSource(@PathParam("id") Long id, SourceUpdateRequest req) {
        SourceDto updated = sourceService.update(id, req);
        if (updated == null) return Response.status(404).build();
        return Response.ok(updated).build();
    }

    /**
     * POST /api/v1/sources/{id}/fetch
     * Trigger an immediate fetch for a single source.
     */
    @POST
    @Path("/{id}/fetch")
    public Response fetchNow(@PathParam("id") Long id) {
        boolean ok = sourceService.triggerFetch(id);
        if (!ok) return Response.status(404).build();
        return Response.ok("{\"message\":\"Fetch triggered for source " + id + "\"}").build();
    }
}
