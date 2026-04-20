CREATE TABLE tag (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    color           VARCHAR(7) DEFAULT '#6366f1'
);

CREATE TABLE article_tag (
    article_id      BIGINT NOT NULL REFERENCES article(id),
    tag_id          BIGINT NOT NULL REFERENCES tag(id),
    tag_source      VARCHAR(10) NOT NULL DEFAULT 'AI',  -- AI, USER
    PRIMARY KEY (article_id, tag_id)
);
