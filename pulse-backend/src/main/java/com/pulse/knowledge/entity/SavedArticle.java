package com.pulse.knowledge.entity;

import com.pulse.article.entity.Article;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_article")
@Getter
@Setter
public class SavedArticle extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false, unique = true)
    public Article article;

    @Column(name = "user_note", columnDefinition = "TEXT")
    public String userNote;

    @Column(nullable = false, length = 20)
    public String status = "UNREAD";   // READ | UNREAD | ARCHIVED

    @Column(name = "saved_at", nullable = false)
    public LocalDateTime savedAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
