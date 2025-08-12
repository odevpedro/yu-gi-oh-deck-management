package com.odevpedro.yugiohcollections.card.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.odevpedro.yugiohcollections.card.application.CardFactory;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardSearchPort;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.hibernate.internal.util.collections.ArrayHelper.slice;

@Component
public class YgoProApiClient implements CardSearchPort {

    private final YgoProFeignClient client;

    public YgoProApiClient(YgoProFeignClient client) {
        this.client = client;
    }

    @Override
    public List<Card> searchByName(String name) {
        String clean = sanitize(name);
        try {
            JsonNode response = client.getCardsByName(clean);
            return mapData(response);
        } catch (FeignException.BadRequest e) {
            return List.of(); // nome exato não encontrado
        }
    }

    @Override
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
    public List<Card> searchByType(String type) {
        String externalType = toExternalType(type); // "Spell Card" | "Trap Card" | "Effect Monster"
        if (externalType == null) return List.of();
        try {
            JsonNode response = client.getCardsByType(externalType);
            return mapData(response);
        } catch (FeignException.BadRequest e) {
            return List.of();
        }
    }

    @Override
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

    /* ===================== Helpers ===================== */

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

    /** Converte SPELL/TRAP/MONSTER (ou variações) para o valor aceito pela API externa. */
    private String toExternalType(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String v = raw.trim().toUpperCase(Locale.ROOT);
        if (v.contains("SPELL"))   return "Spell Card";
        if (v.contains("TRAP"))    return "Trap Card";
        if (v.contains("MONSTER")) return "Effect Monster"; // base para monstros
        return null;
    }

    /** Normaliza sub-tipo (race) para valores aceitos pela API: Equip, Field, Quick-Play, Continuous, Counter. */
    private String normalizeRace(String race) {
        if (race == null || race.isBlank()) return null;
        String r = race.trim();
        if (r.equalsIgnoreCase("quick") || r.equalsIgnoreCase("quick_play") || r.equalsIgnoreCase("quick-play"))
            return "Quick-Play";
        if (r.equalsIgnoreCase("continuous")) return "Continuous";
        if (r.equalsIgnoreCase("counter"))    return "Counter";
        if (r.equalsIgnoreCase("field"))      return "Field";
        if (r.equalsIgnoreCase("equip"))      return "Equip";
        // default: capitaliza primeira letra
        return r.substring(0,1).toUpperCase() + r.substring(1).toLowerCase(Locale.ROOT);
    }
}