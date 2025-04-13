package com.odevpedro.yugiohcollections.deck.adapter.out.persistance;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistance.repository.MonsterCardJpaRepository;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistance.repository.SpellCardJpaRepository;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistance.repository.TrapCardJpaRepository;
import com.odevpedro.yugiohcollections.deck.application.mapper.CardMapper;
import com.odevpedro.yugiohcollections.deck.domain.model.Card;
import com.odevpedro.yugiohcollections.deck.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.deck.domain.model.SpellCard;
import com.odevpedro.yugiohcollections.deck.domain.model.TrapCard;
import com.odevpedro.yugiohcollections.deck.domain.model.ports.CardPersistencePort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardRepositoryAdapter implements CardPersistencePort {

    private final MonsterCardJpaRepository monsterRepo;
    private final SpellCardJpaRepository spellRepo;
    private final TrapCardJpaRepository trapRepo;
    private final CardMapper mapper;

    public CardRepositoryAdapter(
            MonsterCardJpaRepository monsterRepo,
            SpellCardJpaRepository spellRepo,
            TrapCardJpaRepository trapRepo,
            CardMapper mapper) {
        this.monsterRepo = monsterRepo;
        this.spellRepo = spellRepo;
        this.trapRepo = trapRepo;
        this.mapper = mapper;
    }

    @Override
    public Card save(Card card) {
        if (card instanceof MonsterCard monster) {
            var entity = mapper.toEntity(monster);
            return mapper.toDomain(monsterRepo.save(entity));
        }
        if (card instanceof SpellCard spell) {
            var entity = mapper.toEntity(spell);
            return mapper.toDomain(spellRepo.save(entity));
        }
        if (card instanceof TrapCard trap) {
            var entity = mapper.toEntity(trap);
            return mapper.toDomain(trapRepo.save(entity));
        }
        throw new UnsupportedOperationException("Tipo de carta não suportado");
    }

    @Override
    public List<Card> findAllByOwnerId(String ownerId) {
        List<Card> all = new ArrayList<>();

        monsterRepo.findAllByOwnerId(ownerId)
                .stream()
                .map(mapper::toDomain)
                .forEach(all::add);

        spellRepo.findAllByOwnerId(ownerId)
                .stream()
                .map(mapper::toDomain)
                .forEach(all::add);

        trapRepo.findAllByOwnerId(ownerId)
                .stream()
                .map(mapper::toDomain)
                .forEach(all::add);

        return all;

    }
}