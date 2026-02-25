package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.adapter.out.dto.CardResponseDTO;
import com.odevpedro.yugiohcollections.card.domain.model.ports.ExternalCardQueryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindCardsByIdsUseCase {

    private final ExternalCardQueryPort externalQueryPort;

    public FindCardsByIdsUseCase(ExternalCardQueryPort externalQueryPort) {
        this.externalQueryPort = externalQueryPort;
    }

    public List<CardResponseDTO> execute(List<Long> ids) {
        return externalQueryPort.findByIds(ids)
                .stream()
                .map(card -> new CardResponseDTO(
                        card.getId(),
                        card.getName(),
                        card.getType().name(),
                        card.getImageUrl(),
                        card.getDescription()
                ))
                .toList();
    }
}