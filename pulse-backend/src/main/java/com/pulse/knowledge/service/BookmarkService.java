package com.pulse.knowledge.service;

import com.pulse.article.entity.Article;
import com.pulse.article.repository.ArticleRepository;
import com.pulse.knowledge.dto.BookmarkDto;
import com.pulse.knowledge.dto.BookmarkRequest;
import com.pulse.knowledge.dto.BookmarkUpdateRequest;
import com.pulse.knowledge.entity.SavedArticle;
import com.pulse.knowledge.repository.SavedArticleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookmarkService {

    @Inject SavedArticleRepository savedArticleRepository;
    @Inject ArticleRepository articleRepository;

    public List<BookmarkDto> getAll(String status) {
        List<SavedArticle> list = (status != null && !status.isBlank())
                ? savedArticleRepository.findByStatus(status.toUpperCase())
                : savedArticleRepository.findAllRecent();
        return list.stream().map(BookmarkDto::from).toList();
    }

    public Optional<BookmarkDto> getById(Long id) {
        SavedArticle s = savedArticleRepository.findById(id);
        return Optional.ofNullable(s).map(BookmarkDto::from);
    }

    public Optional<BookmarkDto> getByArticleId(Long articleId) {
        return savedArticleRepository.findByArticleId(articleId).map(BookmarkDto::from);
    }

    public boolean isBookmarked(Long articleId) {
        return savedArticleRepository.existsByArticleId(articleId);
    }

    @Transactional
    public Optional<BookmarkDto> create(BookmarkRequest req) {
        if (req.articleId() == null) return Optional.empty();

        // Return existing if already bookmarked
        Optional<SavedArticle> existing = savedArticleRepository.findByArticleId(req.articleId());
        if (existing.isPresent()) return Optional.of(BookmarkDto.from(existing.get()));

        Article article = articleRepository.findById(req.articleId());
        if (article == null) return Optional.empty();

        SavedArticle s = new SavedArticle();
        s.article  = article;
        s.userNote = req.userNote();
        s.status   = "UNREAD";
        savedArticleRepository.persist(s);
        return Optional.of(BookmarkDto.from(s));
    }

    @Transactional
    public Optional<BookmarkDto> update(Long id, BookmarkUpdateRequest req) {
        SavedArticle s = savedArticleRepository.findById(id);
        if (s == null) return Optional.empty();

        // Patch fields
        if (req.userNote() != null) s.userNote = req.userNote();
        if (req.status()   != null && !req.status().isBlank()) {
            s.status = req.status().toUpperCase();
        }
        savedArticleRepository.persist(s);
        return Optional.of(BookmarkDto.from(s));
    }

    @Transactional
    public boolean delete(Long id) {
        SavedArticle s = savedArticleRepository.findById(id);
        if (s == null) return false;
        savedArticleRepository.delete(s);
        return true;
    }

    @Transactional
    public boolean deleteByArticleId(Long articleId) {
        Optional<SavedArticle> s = savedArticleRepository.findByArticleId(articleId);
        if (s.isEmpty()) return false;
        savedArticleRepository.delete(s.get());
        return true;
    }
}
