-- =====================================================
-- card-service: schema inicial
-- Tabelas necessárias para os endpoints em produção
-- =====================================================

-- Catálogo de cartas Monster
CREATE TABLE monster_cards (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000),
    archetype   VARCHAR(255),
    type        VARCHAR(50)   NOT NULL,
    image_url   VARCHAR(500),
    attack      INT,
    defense     INT,
    level       INT,
    attribute   VARCHAR(50),
    monster_type VARCHAR(50),
    owner_id    VARCHAR(100)
);

CREATE TABLE monster_card_subtypes (
    card_id  BIGINT      NOT NULL,
    sub_type VARCHAR(50) NOT NULL
);

-- Catálogo de cartas Spell
CREATE TABLE spell_cards (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000),
    archetype   VARCHAR(255),
    type        VARCHAR(50)   NOT NULL,
    image_url   VARCHAR(500),
    spell_type  VARCHAR(50),
    owner_id    VARCHAR(100)
);

-- Catálogo de cartas Trap
CREATE TABLE trap_cards (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000),
    archetype   VARCHAR(255),
    type        VARCHAR(50)   NOT NULL,
    image_url   VARCHAR(500),
    trap_type   VARCHAR(50),
    owner_id    VARCHAR(100)
);

-- Coleção do usuário (quais cartas ele possui)
CREATE TABLE user_cards (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    VARCHAR(100) NOT NULL,
    card_type  VARCHAR(20)  NOT NULL,
    card_id    BIGINT       NOT NULL,
    quantity   INT          NOT NULL DEFAULT 1,
    notes      VARCHAR(500),
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_card UNIQUE (user_id, card_type, card_id)
);

-- Índices para buscas frequentes
CREATE INDEX idx_user_cards_user ON user_cards(user_id);
CREATE INDEX idx_user_cards_card ON user_cards(card_type, card_id);