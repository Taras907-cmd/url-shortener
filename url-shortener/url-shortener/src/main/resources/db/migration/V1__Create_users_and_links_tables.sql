CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE links (
    id             BIGSERIAL PRIMARY KEY,
    short_code     VARCHAR(8) NOT NULL UNIQUE,
    original_url   VARCHAR(2048) NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at     TIMESTAMP NOT NULL,
    click_count    BIGINT NOT NULL DEFAULT 0,
    user_id        BIGINT NOT NULL,
    CONSTRAINT fk_links_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_links_short_code ON links (short_code);
CREATE INDEX idx_links_user_id ON links (user_id);