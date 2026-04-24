package com.pulse.article.resource;

import com.pulse.article.dto.ArticleDto;
import com.pulse.article.service.ArticleService;
import com.pulse.common.dto.PagedResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/articles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticleResource {

    @Inject
    ArticleService articleService;

    @GET
    public PagedResponse<ArticleDto> getArticles(
            @QueryParam("domain")   String domain,
            @QueryParam("language") String language,
            @QueryParam("category") String category,
            @QueryParam("q")        String search,
            @QueryParam("page")     @DefaultValue("0")  int page,
            @QueryParam("size")     @DefaultValue("20") int size) {

        // Cap size at 100
        size = Math.min(size, 100);
        return articleService.getArticles(domain, language, category, search, page, size);
    }

    @GET
    @Path("/{id}")
    public Response getArticle(@PathParam("id") Long id) {
        ArticleDto dto = articleService.getById(id);
        if (dto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(dto).build();
    }
}
