package com.odevpedro.yugiohcollections.card.adapter.out.persistance;

import com.fasterxml.jackson.databind.JsonNode;
import com.odevpedro.yugiohcollections.card.adapter.out.external.YgoProFeignClient;
import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.MonsterCardEntity;
import com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository.MonsterCardJpaRepository;
import com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository.SpellCardJpaRepository;
import com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository.TrapCardJpaRepository;
import com.odevpedro.yugiohcollections.card.application.dto.CardSummaryDTO;
import com.odevpedro.yugiohcollections.card.application.mapper.CardMapper;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.card.domain.model.SpellCard;
import com.odevpedro.yugiohcollections.card.domain.model.TrapCard;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardPersistencePort;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardQueryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.odevpedro.yugiohcollections.card.domain.model.enums.CardType.*;

@Component
public class CardRepositoryAdapter implements CardPersistencePort, CardQueryPort {

    private final MonsterCardJpaRepository monsterRepo;
    private final SpellCardJpaRepository spellRepo;
    private final TrapCardJpaRepository trapRepo;
    private final CardMapper mapper;
    private final YgoProFeignClient ygoProFeignClient;

    public CardRepositoryAdapter(
            MonsterCardJpaRepository monsterRepo,
            SpellCardJpaRepository spellRepo,
            TrapCardJpaRepository trapRepo,
            CardMapper mapper,
            YgoProFeignClient ygoProFeignClient
    ) {
        this.monsterRepo = monsterRepo;
        this.spellRepo = spellRepo;
        this.trapRepo = trapRepo;
        this.mapper = mapper;
        this.ygoProFeignClient = ygoProFeignClient;
    }

    @Override
    public List<Card> findAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        String csv = ids.stream().distinct()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        JsonNode response = ygoProFeignClient.getCardsByIds(csv);
        return parseCardsFromJson(response);
    }

    private List<Card> parseCardsFromJson(JsonNode response) {
        if (response == null || !response.has("data")) return List.of();

        List<Card> cards = new ArrayList<>();

        for (JsonNode node : response.get("data")) {
            long id = node.get("id").asLong();
            String name = node.get("name").asText();
            String desc = node.has("desc") ? node.get("desc").asText() : "";
            String archetype = node.has("archetype") ? node.get("archetype").asText() : null;
            String typeRaw = node.get("type").asText();
            String imageUrl = node.get("card_images").get(0).get("image_url").asText();

            CardType type = CardType.fromYgoProType(typeRaw);

            Card card = switch (type) {
                case MONSTER -> new MonsterCard(id, name, desc, archetype, type, imageUrl);
                case SPELL   -> new SpellCard(id, name, desc, archetype, type, imageUrl);
                case TRAP    -> new TrapCard(id, name, desc, archetype, type, imageUrl);
            };

            cards.add(card);
        }

        return cards;
    }

    @Override
    public List<Card> findAllByOwnerId(String ownerId) {
        List<Card> all = new ArrayList<>();

        monsterRepo.findAllByOwnerId(ownerId)
                .stream().map(mapper::toDomain).forEach(all::add);

        spellRepo.findAllByOwnerId(ownerId)
                .stream().map(mapper::toDomain).forEach(all::add);

        trapRepo.findAllByOwnerId(ownerId)
                .stream().map(mapper::toDomain).forEach(all::add);

        return all;
    }

    @Override
    public Card save(Card card) {
        if (card instanceof SpellCard s) {
            return spellRepo.findByOwnerIdAndNameIgnoreCaseAndTypeAndSpellType(
                    s.getOwnerId(), s.getName(), s.getType(), s.getSpellType()
            ).map(mapper::toDomain).orElseGet(() -> mapper.toDomain(spellRepo.save(mapper.toEntity(s))));
        }

        if (card instanceof TrapCard t) {
            return trapRepo.findByOwnerIdAndNameIgnoreCaseAndTypeAndTrapType(
                    t.getOwnerId(), t.getName(), t.getType(), t.getTrapType()
            ).map(mapper::toDomain).orElseGet(() -> mapper.toDomain(trapRepo.save(mapper.toEntity(t))));
        }

        if (card instanceof MonsterCard m) {
            return monsterRepo.findByOwnerIdAndNameIgnoreCaseAndType(
                    m.getOwnerId(), m.getName(), m.getType()
            ).map(mapper::toDomain).orElseGet(() -> mapper.toDomain(monsterRepo.save(mapper.toEntity(m))));
        }

        throw new UnsupportedOperationException("Tipo de carta não suportado");
    }

    @Override
    public Page<Card> findAllByType(CardType type, Pageable pageable) {
        return switch (type) {
            case MONSTER -> monsterRepo.findAll(pageable).map(mapper::toDomain);
            case SPELL   -> spellRepo.findAll(pageable).map(mapper::toDomain);
            case TRAP    -> trapRepo.findAll(pageable).map(mapper::toDomain);
        };
    }

    @Override
    public Optional<Card> updateCard(Long id, Card updatedCard) {
        if (updatedCard instanceof MonsterCard monster) {
            return monsterRepo.findById(id)
                    .filter(e -> e.getOwnerId().equals(monster.getOwnerId()))
                    .map(existing -> {
                        var entity = mapper.toEntity(monster);
                        entity.setId(id);
                        return mapper.toDomain(monsterRepo.save(entity));
                    });
        }
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> deleteByIdAndOwner(Long id, String ownerId) {
        if (monsterRepo.findById(id).filter(c -> ownerId.equals(c.getOwnerId())).isPresent()) {
            monsterRepo.deleteById(id);
            return Optional.of(true);
        }
        if (spellRepo.findById(id).filter(c -> ownerId.equals(c.getOwnerId())).isPresent()) {
            spellRepo.deleteById(id);
            return Optional.of(true);
        }
        if (trapRepo.findById(id).filter(c -> ownerId.equals(c.getOwnerId())).isPresent()) {
            trapRepo.deleteById(id);
            return Optional.of(true);
        }
        return Optional.empty();
    }

    private CardSummaryDTO toCardSummaryDTO(Card card) {
        return CardSummaryDTO.builder()
                .cardId(card.getId())
                .name(card.getName())
                .type(card.getType() != null ? card.getType().name() : null)
                .imageUrl(card.getImageUrl())
                .description(card.getDescription())
                .build();
    }
}