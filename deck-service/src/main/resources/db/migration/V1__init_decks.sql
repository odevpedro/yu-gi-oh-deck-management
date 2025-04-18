CREATE TABLE decks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    owner_id VARCHAR(255)
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
