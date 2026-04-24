package com.pulse.ai.entity;

import com.pulse.article.entity.Article;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ai_summary")
@Getter
@Setter
public class AiSummary extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false, unique = true)
    public Article article;

    @Column(nullable = false, length = 1000)
    public String summary;

    // Stored as JSONB — mapped as JSON string, deserialized in DTO layer
    @Column(name = "key_insights", columnDefinition = "jsonb")
    public String keyInsights;       // JSON array: ["insight1", "insight2"]

    @Column(length = 20)
    public String sentiment;         // POSITIVE | NEGATIVE | NEUTRAL

    @Column(name = "sentiment_score")
    public Double sentimentScore;    // -1.0 to 1.0

    @Column(columnDefinition = "jsonb")
    public String tags;              // JSON array: ["tag1", "tag2"]

    @Column(name = "ai_provider", nullable = false, length = 50)
    public String aiProvider;        // OPENAI | CLAUDE

    @Column(name = "model_used", nullable = false, length = 100)
    public String modelUsed;

    @Column(name = "generated_at", nullable = false)
    public LocalDateTime generatedAt = LocalDateTime.now();
}
