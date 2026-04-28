CREATE TABLE token_blacklist (
    id              BIGSERIAL    NOT NULL PRIMARY KEY,
    token_hash      VARCHAR(64)  NOT NULL UNIQUE,
    expires_at      TIMESTAMP    NOT NULL,
    blacklisted_at  TIMESTAMP    NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_token_blacklist_token_hash ON token_blacklist(token_hash);
CREATE INDEX idx_token_blacklist_expires_at ON token_blacklist(expires_at);