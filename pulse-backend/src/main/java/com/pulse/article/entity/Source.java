package com.pulse.article.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "source")
@Getter
@Setter
public class Source extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String url;

    @Column(name = "feed_url", nullable = false)
    public String feedUrl;

    @Column(name = "icon_url")
    public String iconUrl;

    @Column(nullable = false)
    public String type;

    @Column(nullable = false)
    public String domain;

    @Column(nullable = false)
    public String language;

    @Column(nullable = false)
    public String status;

    @Column(name = "last_fetched_at")
    public LocalDateTime lastFetchedAt;

    @Column(name = "fetch_interval_minutes", nullable = false)
    public Integer fetchIntervalMinutes = 30;

    @Column(name = "error_count", nullable = false)
    public Integer errorCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
