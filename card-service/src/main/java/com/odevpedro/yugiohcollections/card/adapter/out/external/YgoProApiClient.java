package com.odevpedro.yugiohcollections.card.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.odevpedro.yugiohcollections.card.application.CardFactory;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
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
        String clean = sanitize(name);
        try {
            JsonNode response = client.getCardsByName(clean);
            return mapData(response);
        } catch (FeignException.BadRequest e) {
            return List.of();
        }
    }

    @Override
    @Cacheable(value = "cardsByFuzzy", key = "T(org.springframework.util.StringUtils).trimAllWhitespace(#fname).toLowerCase()")
    @CircuitBreaker(name = "ygopro", fallbackMethod = "fallbackByFuzzy")
    @Retry(name = "ygopro")
    public List<Card> searchByFuzzyName(String fname) {
        String clean = sanitize(fname);
        try {
            JsonNode response = client.getCardsByFuzzy(clean);
            return mapData(response);
        } catch (FeignException.BadRequest e) {
            return List.of();
        }
    }

    @Override
    @Cacheable(value = "cardsByType", key = "T(java.util.Locale).ROOT, #type == null ? 'null' : #type.toLowerCase()")
    @CircuitBreaker(name = "ygopro", fallbackMethod = "fallbackByType")
    @Retry(name = "ygopro")
    public List<Card> searchByType(String type) {
        String externalType = toExternalType(type);
        if (externalType == null) return List.of();
        try {
            JsonNode response = client.getCardsByType(externalType);
            return mapData(response);
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
            JsonNode response = client.getCardsByTypeAndRace(externalType, externalRace);
            return mapData(response);
        } catch (FeignException.BadRequest e) {
            return List.of();
        }
    }


    private List<Card> fallbackByName(String name, Throwable t) { return List.of(); }
    private List<Card> fallbackByFuzzy(String fname, Throwable t) { return List.of(); }
    private List<Card> fallbackByType(String type, Throwable t) { return List.of(); }
    private List<Card> fallbackByTypeRace(String type, String race, Throwable t) { return List.of(); }


    private List<Card> mapData(JsonNode root) {
        List<Card> cards = new ArrayList<>();
        if (root != null && root.has("data") && root.get("data").isArray()) {
            for (JsonNode node : root.get("data")) {
                CardFactory.fromJson(node).ifPresent(cards::add);
            }
        }
        return cards;
    }

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
        return r.substring(0,1).toUpperCase() + r.substring(1).toLowerCase(Locale.ROOT);
    }
}