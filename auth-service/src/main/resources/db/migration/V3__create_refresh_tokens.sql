CREATE TABLE refresh_tokens (
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    token       VARCHAR(512) NOT NULL UNIQUE,
    user_id     UUID         NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    is_revoked  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);