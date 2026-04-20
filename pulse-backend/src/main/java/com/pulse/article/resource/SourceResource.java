package com.pulse.article.resource;

import com.pulse.article.entity.Source;
import com.pulse.article.repository.SourceRepository;
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
    SourceRepository sourceRepository;

    @GET
    public List<Source> getAllSources() {
        return sourceRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getSource(@PathParam("id") Long id) {
        Source source = sourceRepository.findById(id);
        if (source == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(source).build();
    }
}
