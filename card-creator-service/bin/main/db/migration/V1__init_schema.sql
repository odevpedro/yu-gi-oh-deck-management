CREATE TABLE custom_cards (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_id    VARCHAR(100)  NOT NULL,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(2000) NOT NULL,
    card_type   VARCHAR(20)   NOT NULL, -- MONSTER | SPELL | TRAP
    status      VARCHAR(20)   NOT NULL DEFAULT 'PENDING', -- PENDING | APPROVED | REJECTED
    reject_reason VARCHAR(500),

    -- Campos de Monstro (nullable para Spell/Trap)
    attack      INT,
    defense     INT,
    level       INT,
    attribute   VARCHAR(20),  -- DARK | LIGHT | FIRE | WATER | EARTH | WIND | DIVINE
    monster_type VARCHAR(30), -- DRAGON | SPELLCASTER | etc
    summon_condition VARCHAR(500),

    -- Campos de Spell/Trap
    sub_type    VARCHAR(20),  -- NORMAL | CONTINUOUS | QUICK_PLAY | FIELD | EQUIP | RITUAL | COUNTER

    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_custom_cards_owner  ON custom_cards(owner_id);
CREATE INDEX idx_custom_cards_status ON custom_cards(status);