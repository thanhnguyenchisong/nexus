package com.pulse.knowledge.repository;

import com.pulse.knowledge.entity.Collection;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CollectionRepository implements PanacheRepository<Collection> {

    public List<Collection> findAllByDate() {
        return findAll(Sort.by("createdAt").descending()).list();
    }
}
