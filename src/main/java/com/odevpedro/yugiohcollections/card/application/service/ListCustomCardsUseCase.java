package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardPersistencePort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCustomCardsUseCase {

    private final CardPersistencePort cardPersistencePort;

    public ListCustomCardsUseCase(CardPersistencePort cardPersistencePort) {
        this.cardPersistencePort = cardPersistencePort;
    }

    public List<Card> findAllByOwner(String ownerId) {
        return cardPersistencePort.findAllByOwnerId(ownerId);
    }
}