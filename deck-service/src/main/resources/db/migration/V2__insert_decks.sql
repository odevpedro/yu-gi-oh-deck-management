-- Inserção de decks base
INSERT INTO decks (id, name, owner_id) VALUES
  (1, 'Dark Magician Deck', 'user123'),
  (2, 'Blue-Eyes White Dragon Deck', 'user456'),
  (3, 'Elemental HERO Deck', 'user123'),
  (4, 'Red-Eyes Black Dragon Deck', 'user789'),
  (5, 'Cyber Dragon Deck', 'user456');

-- Inserção de cartas no main deck
INSERT INTO deck_entity_main_deck (deck_entity_id, main_deck) VALUES
  (1, 46986414), -- Dark Magician
  (1, 70781052), -- Magician's Rod
  (1, 98502113), -- Dark Magic Circle

  (2, 89631139), -- Blue-Eyes White Dragon
  (2, 23995346), -- The White Stone of Ancients
  (2, 38517737), -- Blue-Eyes Alternative White Dragon

  (3, 21844576), -- Elemental HERO Neos
  (3, 35809262), -- Elemental HERO Stratos
  (3, 86188410), -- E - Emergency Call

  (4, 74677422), -- Red-Eyes Black Dragon
  (4, 38033121), -- Red-Eyes Insight
  (4, 31305911), -- Red-Eyes Fusion

  (5, 70095154), -- Cyber Dragon
  (5, 74157028), -- Cyber Dragon Core
  (5, 33396948); -- Power Bond

-- Inserção de cartas no extra deck
INSERT INTO deck_entity_extra_deck (deck_entity_id, extra_deck) VALUES
  (1, 98502113), -- The Dark Magicians

  (2, 23995346), -- Blue-Eyes Twin Burst Dragon

  (3, 50720316), -- Elemental HERO Sunrise

  (4, 96561011), -- Red-Eyes Slash Dragon

  (5, 63468625); -- Chimeratech Rampage Dragon

-- Inserção de cartas no side deck
INSERT INTO deck_entity_side_deck (deck_entity_id, side_deck) VALUES
  (1, 53129443), -- Dark Magical Circle
  (2, 55444629), -- Dragon Shrine
  (3, 40061558), -- Mask Change
  (4, 83764719), -- Mystical Space Typhoon
  (5, 91949988); -- Cyber Emergency
