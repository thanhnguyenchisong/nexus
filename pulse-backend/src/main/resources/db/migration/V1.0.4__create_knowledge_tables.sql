CREATE TABLE saved_article (
    id              BIGSERIAL PRIMARY KEY,
    article_id      BIGINT NOT NULL UNIQUE REFERENCES article(id),
    user_note       TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'UNREAD', -- READ, UNREAD, ARCHIVED
    saved_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE collection (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE collection_article (
    collection_id       BIGINT NOT NULL REFERENCES collection(id) ON DELETE CASCADE,
    saved_article_id    BIGINT NOT NULL REFERENCES saved_article(id) ON DELETE CASCADE,
    PRIMARY KEY (collection_id, saved_article_id)
);
