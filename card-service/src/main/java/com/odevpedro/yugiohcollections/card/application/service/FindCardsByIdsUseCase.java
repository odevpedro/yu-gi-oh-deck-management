package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.adapter.out.dto.CardResponseDTO;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardQueryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FindCardsByIdsUseCase {

    private final CardQueryPort cardQueryPort;

    public FindCardsByIdsUseCase(CardQueryPort cardQueryPort) {
        this.cardQueryPort = cardQueryPort;
    }

    public List<CardResponseDTO> execute(List<Long> ids) {
        return cardQueryPort.findAllByIds(ids)
                .stream()
                .map(card -> new CardResponseDTO(
                        card.getId(),
                        card.getName(),
                        card.getType().name(),
                        card.getImageUrl()
                ))
                .collect(Collectors.toList());
    }
}