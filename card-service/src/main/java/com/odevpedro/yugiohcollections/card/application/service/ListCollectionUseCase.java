package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.domain.model.enums.UserCardView;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CollectionQueryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCollectionUseCase {
    private final CollectionQueryPort port;

    public ListCollectionUseCase(CollectionQueryPort port) {
        this.port = port;
    }

    public List<UserCardView> list(String userId) {
        return port.listByUser(userId);
    }
}
