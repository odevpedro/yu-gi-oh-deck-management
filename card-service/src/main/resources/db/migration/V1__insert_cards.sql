CREATE TABLE monster_cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(1000),
    archetype VARCHAR(255),
    type VARCHAR(50),
    image_url VARCHAR(500),
    attack INT,
    defense INT,
    level INT,
    attribute VARCHAR(50),
    monster_type VARCHAR(50),
    owner_id VARCHAR(100)
);

CREATE TABLE monster_card_subtypes (
    card_id BIGINT,
    sub_type VARCHAR(50)
);

INSERT INTO monster_cards (name, description, archetype, type, image_url, attack, defense, level, attribute, monster_type, owner_id)
VALUES
('Blue-Eyes White Dragon', 'This legendary dragon is a powerful engine of destruction.', 'Blue-Eyes', 'MONSTER', 'https://static.cardimg.com/blueeyes.jpg', 3000, 2500, 8, 'LIGHT', 'DRAGON', 'konami'),
('Dark Magician', 'The ultimate wizard in terms of attack and defense.', 'Dark Magician', 'MONSTER', 'https://static.cardimg.com/darkmagician.jpg', 2500, 2100, 7, 'DARK', 'SPELLCASTER', 'konami'),
('Red-Eyes Black Dragon', 'A ferocious dragon with a deadly attack.', 'Red-Eyes', 'MONSTER', 'https://static.cardimg.com/redeyes.jpg', 2400, 2000, 7, 'DARK', 'DRAGON', 'konami');

INSERT INTO monster_card_subtypes (card_id, sub_type)
VALUES
(1, 'NORMAL'),
(2, 'NORMAL'),
(3, 'NORMAL');
