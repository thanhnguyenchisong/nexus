package com.pulse.article.service;

import com.pulse.article.dto.ArticleDto;
import com.pulse.article.entity.Article;
import com.pulse.article.repository.ArticleRepository;
import com.pulse.common.dto.PagedResponse;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ArticleService {

    @Inject
    ArticleRepository articleRepository;

    public PagedResponse<ArticleDto> getArticles(String domain, String language, String category,
                                                  String search, int page, int size) {
        // Build query
        List<Article> articles;
        long total;

        if (search != null && !search.isBlank()) {
            // Full-text search using tsvector
            String query = "from Article a join fetch a.source s " +
                    "where function('to_tsvector', 'english', coalesce(a.title,'') || ' ' || coalesce(a.description,'')) @@ " +
                    "function('to_tsquery', 'english', ?1)";
            // Fallback: simple LIKE search for MVP
            articles = articleRepository
                    .find("lower(title) like ?1 or lower(description) like ?1",
                            Sort.by("publishedAt").descending(),
                            "%" + search.toLowerCase() + "%")
                    .page(Page.of(page, size)).list();
            total = articleRepository.count("lower(title) like ?1 or lower(description) like ?1",
                    "%" + search.toLowerCase() + "%");
        } else if (domain != null && !domain.isBlank()) {
            articles = articleRepository
                    .find("domain", Sort.by("publishedAt").descending(), domain.toUpperCase())
                    .page(Page.of(page, size)).list();
            total = articleRepository.count("domain", domain.toUpperCase());
        } else {
            articles = articleRepository.findAll(Sort.by("publishedAt").descending())
                    .page(Page.of(page, size)).list();
            total = articleRepository.count();
        }

        List<ArticleDto> dtos = articles.stream().map(ArticleDto::from).toList();
        return PagedResponse.of(dtos, page, size, total);
    }

    public ArticleDto getById(Long id) {
        Article article = articleRepository.findById(id);
        return article != null ? ArticleDto.from(article) : null;
    }
}
