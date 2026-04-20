CREATE TABLE ai_summary (
    id              BIGSERIAL PRIMARY KEY,
    article_id      BIGINT NOT NULL UNIQUE REFERENCES article(id),
    summary         VARCHAR(1000) NOT NULL,             -- Capped at 1000 chars
    key_insights    JSONB,                              -- ["insight1", "insight2", ...]
    sentiment       VARCHAR(20),                        -- POSITIVE, NEGATIVE, NEUTRAL
    sentiment_score FLOAT,                              -- -1.0 to 1.0
    tags            JSONB,                              -- ["tag1", "tag2", ...]
    ai_provider     VARCHAR(50) NOT NULL,               -- OPENAI, CLAUDE
    model_used      VARCHAR(100) NOT NULL,
    generated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);
