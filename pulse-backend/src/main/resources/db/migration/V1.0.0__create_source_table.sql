CREATE TABLE source (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    url             VARCHAR(500) NOT NULL,              -- Source homepage
    feed_url        VARCHAR(500) NOT NULL,              -- RSS/API endpoint
    icon_url        VARCHAR(500),                       -- Source favicon/logo
    type            VARCHAR(20) NOT NULL DEFAULT 'RSS', -- RSS, API
    domain          VARCHAR(50) NOT NULL,               -- AI, FINANCE
    language        VARCHAR(10) NOT NULL DEFAULT 'en',  -- en, vi
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, ERROR
    last_fetched_at TIMESTAMP,
    fetch_interval_minutes INT NOT NULL DEFAULT 30,
    error_count     INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
