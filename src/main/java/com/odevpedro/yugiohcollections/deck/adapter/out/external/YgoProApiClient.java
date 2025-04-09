package com.odevpedro.yugiohcollections.deck.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odevpedro.yugiohcollections.deck.application.CardFactory;
import com.odevpedro.yugiohcollections.deck.domain.model.Card;
import com.odevpedro.yugiohcollections.deck.domain.model.ports.CardSearchPort;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class YgoProApiClient implements CardSearchPort {

    private final YgoProFeignClient client;

    public YgoProApiClient(YgoProFeignClient client, ObjectMapper objectMapper) {
        this.client = client;
    }

    @Override
    public List<Card> searchByName(String name) {
        JsonNode response = client.getCardsByName(name);
        List<Card> cards = new ArrayList<>();

        if (response.has("data")) {
            for (JsonNode node : response.get("data")) {
                CardFactory.fromJson(node).ifPresent(cards::add);
            }
        }

        return cards;
    }
}