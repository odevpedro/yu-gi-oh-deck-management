package com.odevpedro.yugiohcollections.service;

import com.odevpedro.yugiohcollections.domain.model.Card;
import com.odevpedro.yugiohcollections.repositories.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> findAll() {
        return cardRepository.findAll();

    }

    public Card save(Card card) {
        return cardRepository.save(card);
    }
}
