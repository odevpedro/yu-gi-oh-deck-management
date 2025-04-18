package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardPersistencePort;
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