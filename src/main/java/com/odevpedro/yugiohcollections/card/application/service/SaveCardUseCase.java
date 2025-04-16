package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.application.CardFactory;
import com.odevpedro.yugiohcollections.card.application.dto.CardInputDTO;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardPersistencePort;
import org.springframework.stereotype.Service;

@Service
public class SaveCardUseCase {

    private final CardPersistencePort persistencePort;

    public SaveCardUseCase(CardPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    public Card execute(CardInputDTO dto) {
        return CardFactory.fromDTO(dto)
                .map(persistencePort::save)
                .orElseThrow(() -> new IllegalArgumentException("Dados inválidos para criação de carta"));
    }
}