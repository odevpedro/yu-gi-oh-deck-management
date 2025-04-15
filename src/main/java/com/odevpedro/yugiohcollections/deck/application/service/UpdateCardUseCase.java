package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.domain.model.Card;
import com.odevpedro.yugiohcollections.deck.domain.model.ports.CardPersistencePort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateCardUseCase {

    private final CardPersistencePort cardPersistencePort;

    public UpdateCardUseCase(CardPersistencePort cardPersistencePort) {
        this.cardPersistencePort = cardPersistencePort;
    }

    public Optional<Card> update(Long id, Card card) {
        return cardPersistencePort.updateCard(id, card);
    }
    }