package com.pulse.knowledge.resource;

import com.pulse.knowledge.dto.BookmarkDto;
import com.pulse.knowledge.dto.CollectionDto;
import com.pulse.knowledge.service.CollectionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/api/v1/collections")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CollectionResource {

    @Inject CollectionService collectionService;

    @GET
    public List<CollectionDto> list() {
        return collectionService.getAll();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return collectionService.getById(id)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(404).build());
    }

    /** GET /api/v1/collections/{id}/articles  — list bookmarks in this collection */
    @GET
    @Path("/{id}/articles")
    public List<BookmarkDto> getArticles(@PathParam("id") Long id) {
        return collectionService.getArticles(id);
    }

    /** POST /api/v1/collections  body: { "name": "...", "description": "..." } */
    @POST
    public Response create(Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            return Response.status(400).entity("{\"message\":\"name is required\"}").build();
        }
        CollectionDto dto = collectionService.create(name, body.get("description"));
        return Response.status(201).entity(dto).build();
    }

    /** PATCH /api/v1/collections/{id} */
    @PATCH
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Map<String, String> body) {
        return collectionService.update(id, body.get("name"), body.get("description"))
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(404).build());
    }

    /** DELETE /api/v1/collections/{id} */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return collectionService.delete(id)
                ? Response.noContent().build()
                : Response.status(404).build();
    }

    /** POST /api/v1/collections/{id}/articles/{savedArticleId} */
    @POST
    @Path("/{id}/articles/{savedArticleId}")
    public Response addArticle(@PathParam("id") Long id,
                               @PathParam("savedArticleId") Long savedArticleId) {
        return collectionService.addArticle(id, savedArticleId)
                ? Response.ok("{\"message\":\"Added\"}").build()
                : Response.status(404).build();
    }

    /** DELETE /api/v1/collections/{id}/articles/{savedArticleId} */
    @DELETE
    @Path("/{id}/articles/{savedArticleId}")
    public Response removeArticle(@PathParam("id") Long id,
                                  @PathParam("savedArticleId") Long savedArticleId) {
        return collectionService.removeArticle(id, savedArticleId)
                ? Response.noContent().build()
                : Response.status(404).build();
    }
}
