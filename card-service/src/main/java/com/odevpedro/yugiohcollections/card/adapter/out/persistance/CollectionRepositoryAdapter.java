package com.odevpedro.yugiohcollections.card.adapter.out.persistance;

import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.UserCardEntity;
import com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository.UserCardJpaRepository;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.UserCardView;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CollectionPersistencePort;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CollectionQueryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CollectionRepositoryAdapter implements CollectionQueryPort, CollectionPersistencePort {

    private final UserCardJpaRepository repo;

    public CollectionRepositoryAdapter(UserCardJpaRepository repo) {
        this.repo = repo;
    }

    private static UserCardView toView(UserCardEntity e) {
        return new UserCardView(e.getId(), e.getUserId(), e.getCardType(), e.getCardId(), e.getQuantity(), e.getNotes());
    }

    @Override
    public List<UserCardView> listByUser(String userId) {
        return repo.findAllByUserIdOrderByIdAsc(userId).stream().map(CollectionRepositoryAdapter::toView).toList();
    }

    @Override
    public Optional<UserCardView> getEntry(String userId, Long entryId) {
        return repo.findById(entryId)
                .filter(e -> e.getUserId().equals(userId))
                .map(CollectionRepositoryAdapter::toView);
    }

    @Override
    public UserCardView addOrIncrement(String userId, CardType cardType, Long cardId, int quantity, String notes) {
        var existing = repo.findByUserIdAndCardTypeAndCardId(userId, cardType, cardId);
        if (existing.isPresent()) {
            var e = existing.get();
            e.setQuantity(Math.max(1, e.getQuantity() + Math.max(1, quantity)));
            if (notes != null) e.setNotes(notes);
            return toView(repo.save(e));
        }
        var e = new UserCardEntity();
        e.setUserId(userId);
        e.setCardType(cardType);
        e.setCardId(cardId);
        e.setQuantity(Math.max(1, quantity));
        e.setNotes(notes);
        return toView(repo.save(e));
    }

    @Override
    public Optional<UserCardView> updateQuantity(String userId, Long entryId, int quantity, String notes) {
        return repo.findById(entryId)
                .filter(e -> e.getUserId().equals(userId))
                .map(e -> {
                    e.setQuantity(Math.max(1, quantity));
                    if (notes != null) e.setNotes(notes);
                    return toView(repo.save(e));
                });
    }

    @Override
    public boolean remove(String userId, Long entryId) {
        int affected = repo.deleteByUserIdAndId(userId, entryId);
        return affected > 0;
    }
}