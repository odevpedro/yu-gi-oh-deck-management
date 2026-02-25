package com.odevpedro.yugiohcollections.card.adapter.out.persistance;

import com.fasterxml.jackson.databind.JsonNode;
import com.odevpedro.yugiohcollections.card.adapter.out.external.YgoProFeignClient;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.card.domain.model.SpellCard;
import com.odevpedro.yugiohcollections.card.domain.model.TrapCard;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.ports.ExternalCardQueryPort;
import feign.Feign;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.SpellType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.TrapType;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class YgoProExternalCardQueryAdapter implements ExternalCardQueryPort {

    private final YgoProFeignClient feign;

    public YgoProExternalCardQueryAdapter(YgoProFeignClient feign) {
        this.feign = feign;
    }

    @Override
    public List<Card> findByExactName(String name) {
        try {
            JsonNode root = feign.getCardsByName(sanitize(name));
            return mapList(root);
        } catch (FeignException.BadRequest e) {
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Card> findByFuzzyName(String fname) {
        try {
            JsonNode root = feign.getCardsByFuzzy(sanitize(fname));
            return mapList(root);
        } catch (FeignException.BadRequest e) {
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<Card> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        String csv = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        try {
            JsonNode root = feign.getCardsByIds(csv);
            return mapList(root);
        } catch (FeignException.BadRequest e) {
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    /* ===== helpers ===== */

    private List<Card> mapList(JsonNode root) {
        JsonNode data = (root != null) ? root.get("data") : null;
        if (data == null || !data.isArray()) return List.of();
        List<Card> out = new ArrayList<>(data.size());
        for (JsonNode n : data) {
            Card card = mapToDomain(n);
            if (card != null) out.add(card);
        }
        return out;
    }

    private Card mapToDomain(JsonNode n) {
        Long id         = longVal(n, "id");
        String name     = text(n, "name");
        String desc     = text(n, "desc");
        String arche    = text(n, "archetype");
        String imageUrl = firstImage(n);
        String typeStr  = text(n, "type");
        String race     = text(n, "race");
        String ownerId  = null;

        CardType cardType = toCardType(typeStr);

        if (cardType == CardType.SPELL) {
            SpellType spellType = toSpellType(race);
            return SpellCard.create(id, name, desc, arche, imageUrl, spellType, ownerId).orElse(null);
        }

        if (cardType == CardType.TRAP) {
            TrapType trapType = toTrapType(race);
            return TrapCard.create(id, name, desc, arche, imageUrl, trapType, ownerId).orElse(null);
        }

        int atk = intVal(n, "atk", 0);
        int def = intVal(n, "def", 0);
        int lvl = intVal(n, "level", 1);

        MonsterAttribute attr = toMonsterAttribute(text(n, "attribute"));
        MonsterType mtype     = toMonsterType(race);
        Set<MonsterSubType> subs = toMonsterSubTypes(typeStr);

        return MonsterCard.create(id, name, desc, arche, imageUrl, atk, def, lvl, attr, mtype, subs, ownerId)
                .orElse(null);
    }

    private String sanitize(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("^\"|\"$", "");
    }

    private String text(JsonNode n, String field) {
        return (n != null && n.hasNonNull(field)) ? n.get(field).asText() : null;
    }

    private Long longVal(JsonNode n, String field) {
        return (n != null && n.hasNonNull(field)) ? n.get(field).asLong() : null;
    }

    private int intVal(JsonNode n, String field, int def) {
        return (n != null && n.hasNonNull(field)) ? n.get(field).asInt() : def;
    }

    private String firstImage(JsonNode n) {
        JsonNode imgs = (n != null) ? n.get("card_images") : null;
        if (imgs != null && imgs.isArray() && imgs.size() > 0) {
            JsonNode first = imgs.get(0);
            if (first.hasNonNull("image_url")) return first.get("image_url").asText();
        }
        return null;
    }

    /* ===== mapeamentos de enums ===== */

    private CardType toCardType(String typeStr) {
        if (typeStr == null) return CardType.MONSTER;
        String s = typeStr.toLowerCase(Locale.ROOT);
        if (s.contains("spell")) return CardType.SPELL;
        if (s.contains("trap"))  return CardType.TRAP;
        return CardType.MONSTER;
    }

    private SpellType toSpellType(String race) {
        if (race == null) return SpellType.NORMAL;
        String s = race.toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        try { return SpellType.valueOf(s); } catch (IllegalArgumentException ignore) {}
        if (s.contains("QUICK")) return SpellType.QUICK_PLAY;
        return SpellType.NORMAL;
    }

    private TrapType toTrapType(String race) {
        if (race == null) return TrapType.NORMAL;
        String s = race.toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        try { return TrapType.valueOf(s); } catch (IllegalArgumentException ignore) {}
        return TrapType.NORMAL;
    }

    private MonsterAttribute toMonsterAttribute(String attr) {
        if (attr == null) return MonsterAttribute.DARK;
        String s = attr.toUpperCase(Locale.ROOT);
        try { return MonsterAttribute.valueOf(s); } catch (IllegalArgumentException e) { return MonsterAttribute.DARK; }
    }

    private MonsterType toMonsterType(String race) {
        if (race == null) return MonsterType.DRAGON;
        String s = race.toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        try { return MonsterType.valueOf(s); } catch (IllegalArgumentException e) { return MonsterType.DRAGON; }
    }

    private Set<MonsterSubType> toMonsterSubTypes(String typeStr) {
        if (typeStr == null) return EnumSet.noneOf(MonsterSubType.class);
        String s = typeStr.toUpperCase(Locale.ROOT);

        EnumSet<MonsterSubType> set = EnumSet.noneOf(MonsterSubType.class);


        markIfContains(set, s, "EFFECT", MonsterSubType.EFFECT);
        markIfContains(set, s, "FUSION", MonsterSubType.FUSION);
        markIfContains(set, s, "RITUAL", MonsterSubType.RITUAL);
        markIfContains(set, s, "SYNCHRO", MonsterSubType.SYNCHRO);
        markIfContains(set, s, "XYZ", MonsterSubType.XYZ);
        markIfContains(set, s, "PENDULUM", MonsterSubType.PENDULUM);
        markIfContains(set, s, "LINK", MonsterSubType.LINK);
        markIfContains(set, s, "TUNER", MonsterSubType.TUNER);
        markIfContains(set, s, "SPIRIT", MonsterSubType.SPIRIT);
        markIfContains(set, s, "TOON", MonsterSubType.TOON);
        markIfContains(set, s, "UNION", MonsterSubType.UNION);
        markIfContains(set, s, "GEMINI", MonsterSubType.GEMINI);
        markIfContains(set, s, "FLIP", MonsterSubType.FLIP);
        markIfContains(set, s, "NORMAL", MonsterSubType.NORMAL);

        return set;
    }

    private void markIfContains(Set<MonsterSubType> set, String haystack, String needle, MonsterSubType value) {
        if (haystack.contains(needle)) set.add(value);
    }

    @Override
    public List<Card> findByType(CardType type) {
        try {
            String externalType = switch (type) {
                case SPELL -> "Spell Card";
                case TRAP  -> "Trap Card";
                default    -> "Effect Monster";
            };
            JsonNode root = feign.getCardsByType(externalType);
            return mapList(root);
        } catch (FeignException.BadRequest e) {
            return List.of();
        }
    }

    @Override
    public List<Card> findByTypeAndRace(CardType type, String race) {
        try {
            String externalType = (type == CardType.SPELL) ? "Spell Card"
                    : (type == CardType.TRAP)  ? "Trap Card"
                    : "Effect Monster";
            JsonNode root = feign.getCardsByTypeAndRace(externalType, race);
            return mapList(root);
        } catch (FeignException.BadRequest e) {
            return List.of();
        }

}
}