package com.odevpedro.yugiohcollections.creator.application.service;

import com.odevpedro.yugiohcollections.creator.adapter.out.messaging.CardEventPublisher;
import com.odevpedro.yugiohcollections.creator.application.dto.CreateCardRequest;
import com.odevpedro.yugiohcollections.creator.domain.model.CustomCard;
import com.odevpedro.yugiohcollections.creator.domain.model.enums.CardStatus;
import com.odevpedro.yugiohcollections.creator.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.creator.domain.model.enums.SpellSubType;
import com.odevpedro.yugiohcollections.creator.domain.model.enums.TrapSubType;
import com.odevpedro.yugiohcollections.creator.domain.model.ports.CustomCardRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomCardService {

    private final CustomCardRepositoryPort repository;
    private final CardEventPublisher eventPublisher;

    public CustomCard create(String ownerId, CreateCardRequest request) {
        CustomCard card = buildCard(ownerId, request);
        CustomCard saved = repository.save(card);

        log.info("Carta criada — id={} tipo={} status=PENDING", saved.getId(), saved.getCardType());
        eventPublisher.publishCardCreated(saved);

        return saved;
    }

    public CustomCard findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carta não encontrada: " + id));
    }

    public List<CustomCard> findAllByOwner(String ownerId) {
        return repository.findAllByOwnerId(ownerId);
    }

    public CustomCard updateStatus(Long id, CardStatus status, String rejectReason) {
        log.info("Atualizando status da carta — id={} status={}", id, status);
        return repository.updateStatus(id, status, rejectReason);
    }

    private CustomCard buildCard(String ownerId, CreateCardRequest req) {
        CardType type = CardType.valueOf(req.cardType().toUpperCase());

        return switch (type) {
            case MONSTER -> CustomCard.createMonster(
                    ownerId,
                    req.name(),
                    req.description(),
                    req.attack(),
                    req.defense(),
                    req.level(),
                    req.attribute(),
                    req.monsterType(),
                    req.summonCondition()
            );
            case SPELL -> CustomCard.createSpell(
                    ownerId,
                    req.name(),
                    req.description(),
                    SpellSubType.valueOf(req.subType().toUpperCase())
            );
            case TRAP -> CustomCard.createTrap(
                    ownerId,
                    req.name(),
                    req.description(),
                    TrapSubType.valueOf(req.subType().toUpperCase())
            );
        };
    }
}