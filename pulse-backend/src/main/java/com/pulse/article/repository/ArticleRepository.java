package com.pulse.article.repository;

import com.pulse.article.entity.Article;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ArticleRepository implements PanacheRepository<Article> {
    
    public List<Article> findByDomainRecent(String domain, int page, int size) {
        return find("domain", Sort.by("publishedAt").descending(), domain)
                .page(Page.of(page, size))
                .list();
    }
    
    public List<Article> findRecent(int page, int size) {
        return findAll(Sort.by("publishedAt").descending())
                .page(Page.of(page, size))
                .list();
    }
    
    public Article findByOriginalUrl(String originalUrl) {
        return find("originalUrl", originalUrl).firstResult();
    }
}
