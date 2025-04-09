package com.odevpedro.yugiohcollections.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odevpedro.yugiohcollections.domain.model.Card;
import com.odevpedro.yugiohcollections.domain.model.ports.CardSearchPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Component
public class YgoProApiClient implements CardSearchPort {

    
    private final YgoProFeignClient client;
    private final ObjectMapper objectMapper;


    @Override
    public List<Card> searchByName(String name) {
        JsonNode response = client.getCardsByName(name);
        List<Card> cards = new ArrayList<>();

        if (response.has("data")) {
            for (JsonNode node : response.get("data")) {
                CardFactory.fromJson(node).ifPresent(cards::add);
            }
        }
        System.out.println(cards);
        return cards;


    }
}
