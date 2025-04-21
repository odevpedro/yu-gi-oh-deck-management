ALTER TABLE deck_entity_main_deck
ADD CONSTRAINT fk_main_deck FOREIGN KEY (deck_entity_id) REFERENCES decks(id);

ALTER TABLE deck_entity_extra_deck
ADD CONSTRAINT fk_extra_deck FOREIGN KEY (deck_entity_id) REFERENCES decks(id);

ALTER TABLE deck_entity_side_deck
ADD CONSTRAINT fk_side_deck FOREIGN KEY (deck_entity_id) REFERENCES decks(id);
