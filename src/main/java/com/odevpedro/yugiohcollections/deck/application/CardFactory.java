package com.odevpedro.yugiohcollections.deck.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.odevpedro.yugiohcollections.deck.domain.model.Card;
import com.odevpedro.yugiohcollections.deck.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.deck.domain.model.SpellCard;
import com.odevpedro.yugiohcollections.deck.domain.model.TrapCard;
import com.odevpedro.yugiohcollections.deck.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.deck.domain.model.enums.MonsterType;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.SpellType;
import com.odevpedro.yugiohcollections.deck.domain.model.enums.TrapType;

public class CardFactory {

    public static Optional<Card> fromJson(JsonNode node) {
        String type = node.get("type").asText();
        Long id = node.get("id").asLong();
        String name = node.get("name").asText();
        String desc = node.get("desc").asText();
        String archetype = node.has("archetype") ? node.get("archetype").asText() : null;
        String image = node.get("card_images").get(0).get("image_url").asText();

        if (type.contains("Monster")) {
            int atk = node.path("atk").asInt();
            int def = node.path("def").asInt();
            int level = node.path("level").asInt();
            MonsterAttribute attr = MonsterAttribute.valueOf(node.get("attribute").asText().toUpperCase());
            MonsterType monsterType = MonsterType.valueOf(normalize(node.get("race").asText()));
            Set<MonsterSubType> subTypes = detectMonsterSubtypes(type);

            return MonsterCard.create(id, name, desc, archetype, image, atk, def, level, attr, monsterType, subTypes)
                    .map(c -> (Card) c);
        }

        if (type.contains("Spell")) {
            SpellType spellType = SpellType.valueOf(normalize(node.get("race").asText()));
            return SpellCard.create(id, name, desc, archetype, image, spellType).map(c -> (Card) c);
        }

        if (type.contains("Trap")) {
            TrapType trapType = TrapType.valueOf(normalize(node.get("race").asText()));
            return TrapCard.create(id, name, desc, archetype, image, trapType)
                    .map(card -> card); // ✅ ou .map(Function.identity())
        }

        return Optional.empty();
    }

    private static String normalize(String input) {
        return input.trim().toUpperCase().replace("-", "_").replace(" ", "_");
    }

    private static Set<MonsterSubType> detectMonsterSubtypes(String rawType) {
        Set<MonsterSubType> set = EnumSet.noneOf(MonsterSubType.class);
        String[] parts = rawType.toUpperCase().split(" ");
        for (String part : parts) {
            try {
                set.add(MonsterSubType.valueOf(part));
            } catch (IllegalArgumentException ignored) {}
        }
        return set;
    }
}