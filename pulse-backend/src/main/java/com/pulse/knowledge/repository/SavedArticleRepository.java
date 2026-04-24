package com.pulse.knowledge.repository;

import com.pulse.knowledge.entity.SavedArticle;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SavedArticleRepository implements PanacheRepository<SavedArticle> {

    public Optional<SavedArticle> findByArticleId(Long articleId) {
        return find("article.id", articleId).firstResultOptional();
    }

    public boolean existsByArticleId(Long articleId) {
        return count("article.id", articleId) > 0;
    }

    public List<SavedArticle> findByStatus(String status) {
        return find("status", Sort.by("savedAt").descending(), status).list();
    }

    public List<SavedArticle> findAllRecent() {
        return findAll(Sort.by("savedAt").descending()).list();
    }
}
