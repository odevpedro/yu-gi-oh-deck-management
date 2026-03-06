CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE players (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID        NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    location     GEOMETRY(Point, 4326) NOT NULL,
    duel_status  VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    updated_at   TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE player_platforms (
    player_id UUID        NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    platform  VARCHAR(50) NOT NULL
);

CREATE TABLE challenges (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    challenger_id       UUID        NOT NULL,
    target_id           UUID        NOT NULL,
    challenger_deck_id  BIGINT,
    message             VARCHAR(255),
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    expires_at          TIMESTAMP   NOT NULL
);

CREATE INDEX idx_players_location   ON players USING GIST(location);
CREATE INDEX idx_players_user_id    ON players(user_id);
CREATE INDEX idx_players_status     ON players(duel_status);
CREATE INDEX idx_challenges_target  ON challenges(target_id, status);
CREATE INDEX idx_challenges_expires ON challenges(expires_at) WHERE status = 'PENDING';
