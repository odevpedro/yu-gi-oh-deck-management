CREATE TABLE spell_cards (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(1000),
    archetype VARCHAR(255),
    type VARCHAR(50),
    image_url VARCHAR(500),
    spell_type VARCHAR(50),
    owner_id VARCHAR(100)
);

CREATE TABLE trap_cards (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(1000),
    archetype VARCHAR(255),
    type VARCHAR(50),
    image_url VARCHAR(500),
    trap_type VARCHAR(50),
    owner_id VARCHAR(100)
);
