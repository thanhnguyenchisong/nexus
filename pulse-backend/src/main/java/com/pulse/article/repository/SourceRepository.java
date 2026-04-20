package com.pulse.article.repository;

import com.pulse.article.entity.Source;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SourceRepository implements PanacheRepository<Source> {
    public Source findByFeedUrl(String feedUrl) {
        return find("feedUrl", feedUrl).firstResult();
    }
}
