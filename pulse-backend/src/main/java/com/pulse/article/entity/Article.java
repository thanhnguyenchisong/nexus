package com.pulse.article.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "article")
@Getter
@Setter
public class Article extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    public Source source;

    @Column(nullable = false, length = 500)
    public String title;

    @Column(length = 500)
    public String description;

    @Column(name = "original_url", nullable = false, length = 1000, unique = true)
    public String originalUrl;

    @Column(length = 255)
    public String author;

    @Column(name = "thumbnail_url", length = 1000)
    public String thumbnailUrl;

    @Column(nullable = false, length = 50)
    public String domain;

    @Column(length = 100)
    public String category;

    @Column(nullable = false, length = 10)
    public String language = "en";

    @Column(name = "published_at")
    public LocalDateTime publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
}
