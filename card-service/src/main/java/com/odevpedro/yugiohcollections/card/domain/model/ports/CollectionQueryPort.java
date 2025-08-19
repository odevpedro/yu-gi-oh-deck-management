package com.odevpedro.yugiohcollections.card.domain.model.ports;
import com.odevpedro.yugiohcollections.card.domain.model.enums.UserCardView;

import java.util.List;
import java.util.Optional;

public interface CollectionQueryPort {
    List<UserCardView> listByUser(String userId);
    Optional<UserCardView> getEntry(String userId, Long entryId);
}