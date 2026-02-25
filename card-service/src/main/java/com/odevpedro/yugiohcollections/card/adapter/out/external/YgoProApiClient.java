package com.odevpedro.yugiohcollections.card.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.card.domain.model.SpellCard;
import com.odevpedro.yugiohcollections.card.domain.model.TrapCard;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardSearchPort;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class YgoProApiClient implements CardSearchPort {

    private final YgoProFeignClient client;

    public YgoProApiClient(YgoProFeignClient client) {
        this.client = client;
    }

    @Override
    @Cacheable(cacheNames = "cardsByName",
            key = "T(org.springframework.util.StringUtils).trimAllWhitespace(#name)?.toLowerCase()")
    @CircuitBreaker(name = "ygopro", fallbackMethod = "fallbackByName")
    @Retry(name = "ygopro")
    public List<Card> searchByName(String name) {
        try {
            return mapData(client.getCardsByName(sanitize(name)));
        } catch (FeignException.BadRequest e) {
            return List.of();
        }
    }

    @Override
    @Cacheable(value = "cardsByFuzzy",
            key = "T(org.springframework.util.StringUtils).trimAllWhitespace(#fname).toLowerCase()")
    @CircuitBreaker(name = "ygopro", fallbackMethod = "fallbackByFuzzy")
    @Retry(name = "ygopro")
    public List<Card> searchByFuzzyName(String fname) {
        try {
            return mapData(client.getCardsByFuzzy(sanitize(fname)));
        } catch (FeignException.BadRequest e) {
            return List.of();
        }
    }

    @Override
    @Cacheable(value = "cardsByType",
            key = "T(java.util.Locale).ROOT, #type == null ? 'null' : #type.toLowerCase()")
    @CircuitBreaker(name = "ygopro", fallbackMethod = "fallbackByType")
    @Retry(name = "ygopro")
    public List<Card> searchByType(String type) {
        String externalType = toExternalType(type);
        if (externalType == null) return List.of();
        try {
            return mapData(client.getCardsByType(externalType));
        } catch (FeignException.BadRequest e) {
            return List.of();
        }
    }

    @Override
    @Cacheable(value = "cardsByTypeRace",
            key = "((#type==null)?'':#type.toLowerCase()) + '|' + ((#race==null)?'':#race.toLowerCase())")
    @CircuitBreaker(name = "ygopro", fallbackMethod = "fallbackByTypeRace")
    @Retry(name = "ygopro")
    public List<Card> searchByTypeAndRace(String type, String race) {
        String externalType = toExternalType(type);
        String externalRace = normalizeRace(race);
        if (externalType == null || externalRace == null) return List.of();
        try {
            return mapData(client.getCardsByTypeAndRace(externalType, externalRace));
        } catch (FeignException.BadRequest e) {
            return List.of();
        }
    }

    // ===== fallbacks =====

    private List<Card> fallbackByName(String name, Throwable t)                   { return List.of(); }
    private List<Card> fallbackByFuzzy(String fname, Throwable t)                 { return List.of(); }
    private List<Card> fallbackByType(String type, Throwable t)                   { return List.of(); }
    private List<Card> fallbackByTypeRace(String type, String race, Throwable t)  { return List.of(); }

    // ===== parsing =====

    private List<Card> mapData(JsonNode root) {
        if (root == null || !root.has("data") || !root.get("data").isArray()) return List.of();

        List<Card> cards = new ArrayList<>();
        for (JsonNode node : root.get("data")) {
            Card card = parseCard(node);
            if (card != null) cards.add(card);
        }
        return cards;
    }

    private Card parseCard(JsonNode node) {
        Long id       = node.hasNonNull("id")   ? node.get("id").asLong()   : null;
        String name   = node.hasNonNull("name") ? node.get("name").asText() : null;
        String desc   = node.hasNonNull("desc") ? node.get("desc").asText() : null;
        String arche  = node.hasNonNull("archetype") ? node.get("archetype").asText() : null;
        String image  = firstImage(node);
        String typeStr = node.hasNonNull("type") ? node.get("type").asText() : null;

        CardType cardType = toCardType(typeStr);

        return switch (cardType) {
            case SPELL   -> new SpellCard(id, name, desc, arche, cardType, image);
            case TRAP    -> new TrapCard(id, name, desc, arche, cardType, image);
            case MONSTER -> new MonsterCard(id, name, desc, arche, cardType, image);
        };
    }

    private String firstImage(JsonNode node) {
        JsonNode imgs = node.get("card_images");
        if (imgs != null && imgs.isArray() && !imgs.isEmpty()) {
            JsonNode first = imgs.get(0);
            if (first.hasNonNull("image_url")) return first.get("image_url").asText();
        }
        return null;
    }

    private CardType toCardType(String typeStr) {
        if (typeStr == null) return CardType.MONSTER;
        String s = typeStr.toLowerCase(Locale.ROOT);
        if (s.contains("spell")) return CardType.SPELL;
        if (s.contains("trap"))  return CardType.TRAP;
        return CardType.MONSTER;
    }

    // ===== helpers =====

    private String sanitize(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("^\"|\"$", "");
    }

    private String toExternalType(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String v = raw.trim().toUpperCase(Locale.ROOT);
        if (v.contains("SPELL"))   return "Spell Card";
        if (v.contains("TRAP"))    return "Trap Card";
        if (v.contains("MONSTER")) return "Effect Monster";
        return null;
    }

    private String normalizeRace(String race) {
        if (race == null || race.isBlank()) return null;
        String r = race.trim();
        if (r.equalsIgnoreCase("quick") || r.equalsIgnoreCase("quick_play") || r.equalsIgnoreCase("quick-play"))
            return "Quick-Play";
        if (r.equalsIgnoreCase("continuous")) return "Continuous";
        if (r.equalsIgnoreCase("counter"))    return "Counter";
        if (r.equalsIgnoreCase("field"))      return "Field";
        if (r.equalsIgnoreCase("equip"))      return "Equip";
        return r.substring(0, 1).toUpperCase() + r.substring(1).toLowerCase(Locale.ROOT);
    }
}