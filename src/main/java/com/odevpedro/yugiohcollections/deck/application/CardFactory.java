package com.odevpedro.yugiohcollections.deck.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.odevpedro.yugiohcollections.deck.application.dto.CardInputDTO;
import com.odevpedro.yugiohcollections.deck.domain.model.Card;
import com.odevpedro.yugiohcollections.deck.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.deck.domain.model.SpellCard;
import com.odevpedro.yugiohcollections.deck.domain.model.TrapCard;
import com.odevpedro.yugiohcollections.deck.domain.model.enums.*;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

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

            //cria e faz um cast pro tipo mais abstrato
            return MonsterCard.create(id, name, desc, archetype, image, atk, def, level, attr, monsterType, subTypes, "external import")
                    .map(c -> (Card) c);
        }

        if (type.contains("Spell")) {
            SpellType spellType = SpellType.valueOf(normalize(node.get("race").asText()));
            return SpellCard.create(id, name, desc, archetype, image, spellType, "external import").map(c -> (Card) c);
        }

        if (type.contains("Trap")) {
            TrapType trapType = TrapType.valueOf(normalize(node.get("race").asText()));
            return TrapCard.create(id, name, desc, archetype, image, trapType,"external import")
                    .map(card -> card);
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

    public static Optional<Card> fromDTO(CardInputDTO dto) {
        if (dto.getType() == null || dto.getName() == null || dto.getName().isBlank()) {
            return Optional.empty();
        }

        if (dto.getType() == CardType.MONSTER) {
            return MonsterCard.create(
                    null,
                    dto.getName(),
                    dto.getDescription(),
                    dto.getArchetype(),
                    dto.getImageUrl(),
                    dto.getAttack(),
                    dto.getDefense(),
                    dto.getLevel(),
                    dto.getAttribute(),
                    dto.getMonsterType(),
                    dto.getSubTypes(),
                    dto.getOwnerId()
            ).map(c -> (Card) c);
        }

        if (dto.getType() == CardType.SPELL) {
            return SpellCard.create(
                    null,
                    dto.getName(),
                    dto.getDescription(),
                    dto.getArchetype(),
                    dto.getImageUrl(),
                    dto.getSpellType(),
                    dto.getOwnerId()
            ).map(c -> (Card) c);
        }

        if (dto.getType() == CardType.TRAP) {
            return TrapCard.create(
                    null,
                    dto.getName(),
                    dto.getDescription(),
                    dto.getArchetype(),
                    dto.getImageUrl(),
                    dto.getTrapType(),
                    dto.getOwnerId()
            ).map(c -> (Card) c);
        }

        return Optional.empty();
    }
}