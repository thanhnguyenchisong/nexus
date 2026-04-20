CREATE TABLE article (
    id              BIGSERIAL PRIMARY KEY,
    source_id       BIGINT NOT NULL REFERENCES source(id),
    title           VARCHAR(500) NOT NULL,
    description     VARCHAR(500),                       -- Short preview from RSS <description>
    original_url    VARCHAR(1000) NOT NULL UNIQUE,      -- Link to original article
    author          VARCHAR(255),
    thumbnail_url   VARCHAR(1000),                      -- Image from RSS <media:content>
    domain          VARCHAR(50) NOT NULL,               -- AI, FINANCE
    category        VARCHAR(100),
    language        VARCHAR(10) NOT NULL DEFAULT 'en',
    published_at    TIMESTAMP,
    search_vector   TSVECTOR,                           -- FTS on title + description
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_article_domain ON article(domain);
CREATE INDEX idx_article_source_id ON article(source_id);
CREATE INDEX idx_article_published_at ON article(published_at DESC);
CREATE INDEX idx_article_search ON article USING GIN(search_vector);
