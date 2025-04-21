CREATE TABLE decks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_id VARCHAR(255) NOT NULL
);

CREATE TABLE deck_entity_main_deck (
    deck_entity_id BIGINT,
    main_deck BIGINT
);

CREATE TABLE deck_entity_extra_deck (
    deck_entity_id BIGINT,
    extra_deck BIGINT
);

CREATE TABLE deck_entity_side_deck (
    deck_entity_id BIGINT,
    side_deck BIGINT
);