package com.pulse.article.resource;

import com.pulse.article.entity.Article;
import com.pulse.article.repository.ArticleRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/v1/articles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticleResource {

    @Inject
    ArticleRepository articleRepository;

    @GET
    public List<Article> getArticles(
            @QueryParam("domain") String domain,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        if (domain != null && !domain.isBlank()) {
            return articleRepository.findByDomainRecent(domain, page, size);
        }
        return articleRepository.findRecent(page, size);
    }

    @GET
    @Path("/{id}")
    public Response getArticle(@PathParam("id") Long id) {
        Article article = articleRepository.findById(id);
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(article).build();
    }
}
