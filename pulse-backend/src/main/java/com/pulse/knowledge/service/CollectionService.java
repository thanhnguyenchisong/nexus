package com.pulse.knowledge.service;

import com.pulse.knowledge.dto.BookmarkDto;
import com.pulse.knowledge.dto.CollectionDto;
import com.pulse.knowledge.entity.Collection;
import com.pulse.knowledge.entity.SavedArticle;
import com.pulse.knowledge.repository.CollectionRepository;
import com.pulse.knowledge.repository.SavedArticleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CollectionService {

    @Inject CollectionRepository collectionRepository;
    @Inject SavedArticleRepository savedArticleRepository;

    public List<CollectionDto> getAll() {
        return collectionRepository.findAllByDate().stream().map(CollectionDto::from).toList();
    }

    public Optional<CollectionDto> getById(Long id) {
        Collection c = collectionRepository.findById(id);
        return Optional.ofNullable(c).map(CollectionDto::from);
    }

    public List<BookmarkDto> getArticles(Long collectionId) {
        Collection c = collectionRepository.findById(collectionId);
        if (c == null) return List.of();
        return c.savedArticles.stream().map(BookmarkDto::from).toList();
    }

    @Transactional
    public CollectionDto create(String name, String description) {
        Collection c = new Collection();
        c.name = name;
        c.description = description;
        collectionRepository.persist(c);
        return CollectionDto.from(c);
    }

    @Transactional
    public Optional<CollectionDto> update(Long id, String name, String description) {
        Collection c = collectionRepository.findById(id);
        if (c == null) return Optional.empty();
        if (name != null && !name.isBlank()) c.name = name;
        if (description != null) c.description = description;
        collectionRepository.persist(c);
        return Optional.of(CollectionDto.from(c));
    }

    @Transactional
    public boolean delete(Long id) {
        Collection c = collectionRepository.findById(id);
        if (c == null) return false;
        collectionRepository.delete(c);
        return true;
    }

    @Transactional
    public boolean addArticle(Long collectionId, Long savedArticleId) {
        Collection c = collectionRepository.findById(collectionId);
        SavedArticle s = savedArticleRepository.findById(savedArticleId);
        if (c == null || s == null) return false;
        if (!c.savedArticles.contains(s)) c.savedArticles.add(s);
        collectionRepository.persist(c);
        return true;
    }

    @Transactional
    public boolean removeArticle(Long collectionId, Long savedArticleId) {
        Collection c = collectionRepository.findById(collectionId);
        if (c == null) return false;
        c.savedArticles.removeIf(s -> s.id.equals(savedArticleId));
        collectionRepository.persist(c);
        return true;
    }
}
