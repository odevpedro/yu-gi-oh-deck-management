package com.odevpedro.yugiohcollections.creator.domain.model.ports;

import com.odevpedro.yugiohcollections.creator.domain.model.CustomCard;
import com.odevpedro.yugiohcollections.creator.domain.model.enums.CardStatus;

import java.util.List;
import java.util.Optional;

public interface CustomCardRepositoryPort {
    CustomCard save(CustomCard card);
    Optional<CustomCard> findById(Long id);
    List<CustomCard> findAllByOwnerId(String ownerId);
    CustomCard updateStatus(Long id, CardStatus status, String rejectReason);
}