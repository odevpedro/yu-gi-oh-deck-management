package com.odevpedro.yugiohcollections.card.adapter.out.persistance;

import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.MonsterCardEntity;
import com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository.MonsterCardJpaRepository;
import com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository.SpellCardJpaRepository;
import com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository.TrapCardJpaRepository;
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

@Component
public class CardRepositoryAdapter implements CardPersistencePort, CardQueryPort {

    private final MonsterCardJpaRepository monsterRepo;
    private final SpellCardJpaRepository spellRepo;
    private final TrapCardJpaRepository trapRepo;
    private final CardMapper mapper;

    public CardRepositoryAdapter(
            MonsterCardJpaRepository monsterRepo,
            SpellCardJpaRepository spellRepo,
            TrapCardJpaRepository trapRepo,
            CardMapper mapper
    ) {
        this.monsterRepo = monsterRepo;
        this.spellRepo = spellRepo;
        this.trapRepo = trapRepo;
        this.mapper = mapper;
    }

    @Override
    public Card save(Card card) {
        if (card instanceof SpellCard s) {
            // Idempotente: se já existir (ownerId+name+type+spellType), retorna o existente
            var existing = spellRepo.findByOwnerIdAndNameIgnoreCaseAndTypeAndSpellType(
                    s.getOwnerId(), s.getName(), s.getType(), s.getSpellType()
            );
            if (existing.isPresent()) return mapper.toDomain(existing.get());

            var entity = mapper.toEntity(s);
            return mapper.toDomain(spellRepo.save(entity));
        }

        if (card instanceof TrapCard t) {
            var existing = trapRepo.findByOwnerIdAndNameIgnoreCaseAndTypeAndTrapType(
                    t.getOwnerId(), t.getName(), t.getType(), t.getTrapType()
            );
            if (existing.isPresent()) return mapper.toDomain(existing.get());

            var entity = mapper.toEntity(t);
            return mapper.toDomain(trapRepo.save(entity));
        }

        if (card instanceof MonsterCard m) {
            var existing = monsterRepo.findByOwnerIdAndNameIgnoreCaseAndType(
                    m.getOwnerId(), m.getName(), m.getType()
            );
            if (existing.isPresent()) return mapper.toDomain(existing.get());

            var entity = mapper.toEntity(m);
            return mapper.toDomain(monsterRepo.save(entity));
        }

        throw new UnsupportedOperationException("Tipo de carta não suportado");
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
    public List<Card> findAllByIds(List<Long> ids) {
        List<Card> all = new ArrayList<>();

        monsterRepo.findAllById(ids)
                .stream().map(mapper::toDomain).forEach(all::add);

        spellRepo.findAllById(ids)
                .stream().map(mapper::toDomain).forEach(all::add);

        trapRepo.findAllById(ids)
                .stream().map(mapper::toDomain).forEach(all::add);

        return all;
    }

    @Override
    public Page<Card> findAllByType(CardType type, Pageable pageable) {
        return switch (type) {
            case MONSTER -> monsterRepo.findAll(pageable).map(mapper::toDomain);
            case SPELL   -> spellRepo.findAll(pageable).map(mapper::toDomain);
            case TRAP    -> trapRepo.findAll(pageable).map(mapper::toDomain);
        };
    }

    // -------------------- Atualização / Exclusão --------------------

    @Override
    public Optional<Card> updateCard(Long id, Card updatedCard) {
        if (updatedCard instanceof MonsterCard monster) {
            return monsterRepo.findById(id)
                    .filter(e -> e.getOwnerId().equals(monster.getOwnerId()))
                    .map(existing -> {
                        MonsterCardEntity entity = mapper.toEntity(monster);
                        entity.setId(id); // Força a atualização
                        return mapper.toDomain(monsterRepo.save(entity));
                    });
        }
        // (poderia implementar Spell/Trap se necessário)
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
}
