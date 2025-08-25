CREATE TABLE deck_card_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deck_id BIGINT NOT NULL,
    card_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    zone VARCHAR(20) NOT NULL, -- esperado: 'MAIN', 'EXTRA', 'SIDE'

    CONSTRAINT fk_deck_card_entry_deck
        FOREIGN KEY (deck_id) REFERENCES decks(id)
        ON DELETE CASCADE
);

-- Índices recomendados
CREATE INDEX idx_deck_card_entry_deck ON deck_card_entry(deck_id);
CREATE INDEX idx_deck_card_entry_card ON deck_card_entry(card_id);
CREATE INDEX idx_deck_card_entry_zone ON deck_card_entry(zone);
