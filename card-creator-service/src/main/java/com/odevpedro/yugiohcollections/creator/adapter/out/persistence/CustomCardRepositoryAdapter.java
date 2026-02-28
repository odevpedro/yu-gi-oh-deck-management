package com.odevpedro.yugiohcollections.creator.adapter.out.persistence;

import com.odevpedro.yugiohcollections.creator.adapter.out.persistence.repository.CustomCardJpaRepository;
import com.odevpedro.yugiohcollections.creator.application.mapper.CustomCardMapper;
import com.odevpedro.yugiohcollections.creator.domain.model.CustomCard;
import com.odevpedro.yugiohcollections.creator.domain.model.enums.CardStatus;
import com.odevpedro.yugiohcollections.creator.domain.model.ports.CustomCardRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomCardRepositoryAdapter implements CustomCardRepositoryPort {

    private final CustomCardJpaRepository repository;
    private final CustomCardMapper mapper;

    @Override
    public CustomCard save(CustomCard card) {
        return mapper.toDomain(repository.save(mapper.toEntity(card)));
    }

    @Override
    public Optional<CustomCard> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CustomCard> findAllByOwnerId(String ownerId) {
        return repository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public CustomCard updateStatus(Long id, CardStatus status, String rejectReason) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carta não encontrada: " + id));
        entity.setStatus(status);
        entity.setRejectReason(rejectReason);
        return mapper.toDomain(repository.save(entity));
    }
}