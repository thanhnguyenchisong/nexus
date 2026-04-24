package com.pulse.knowledge.resource;

import com.pulse.knowledge.dto.BookmarkDto;
import com.pulse.knowledge.dto.BookmarkRequest;
import com.pulse.knowledge.dto.BookmarkUpdateRequest;
import com.pulse.knowledge.service.BookmarkService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/v1/bookmarks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookmarkResource {

    @Inject BookmarkService bookmarkService;

    /** GET /api/v1/bookmarks?status=UNREAD */
    @GET
    public List<BookmarkDto> list(@QueryParam("status") String status) {
        return bookmarkService.getAll(status);
    }

    /** GET /api/v1/bookmarks/{id} */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return bookmarkService.getById(id)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(404).build());
    }

    /** GET /api/v1/bookmarks/by-article/{articleId} */
    @GET
    @Path("/by-article/{articleId}")
    public Response getByArticle(@PathParam("articleId") Long articleId) {
        return bookmarkService.getByArticleId(articleId)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(404).build());
    }

    /** POST /api/v1/bookmarks  — create bookmark */
    @POST
    public Response create(BookmarkRequest req) {
        return bookmarkService.create(req)
                .map(dto -> Response.status(201).entity(dto).build())
                .orElse(Response.status(404)
                        .entity("{\"message\":\"Article not found\"}")
                        .build());
    }

    /** PATCH /api/v1/bookmarks/{id}  — update note/status */
    @PATCH
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, BookmarkUpdateRequest req) {
        return bookmarkService.update(id, req)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(404).build());
    }

    /** DELETE /api/v1/bookmarks/{id} */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean ok = bookmarkService.delete(id);
        return ok ? Response.noContent().build() : Response.status(404).build();
    }

    /** DELETE /api/v1/bookmarks/by-article/{articleId}  — unbookmark */
    @DELETE
    @Path("/by-article/{articleId}")
    public Response deleteByArticle(@PathParam("articleId") Long articleId) {
        boolean ok = bookmarkService.deleteByArticleId(articleId);
        return ok ? Response.noContent().build() : Response.status(404).build();
    }
}
