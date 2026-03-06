package com.odevpedro.yugiohcollections.community.adapter.out.persistence;

import com.odevpedro.yugiohcollections.community.adapter.out.persistence.repository.ChallengeJpaRepository;
import com.odevpedro.yugiohcollections.community.application.mapper.ChallengeMapper;
import com.odevpedro.yugiohcollections.community.domain.model.Challenge;
import com.odevpedro.yugiohcollections.community.domain.model.ChallengeStatus;
import com.odevpedro.yugiohcollections.community.domain.port.ChallengeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChallengeRepositoryAdapter implements ChallengeRepositoryPort {

    private final ChallengeJpaRepository jpaRepository;
    private final ChallengeMapper mapper;

    @Override
    public Challenge save(Challenge challenge) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(challenge)));
    }

    @Override
    public Optional<Challenge> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Challenge> findPendingByTargetId(UUID targetId) {
        return jpaRepository.findByTargetIdAndStatus(targetId, ChallengeStatus.PENDING)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Challenge> findExpired() {
        return jpaRepository.findExpired(LocalDateTime.now())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void updateStatus(UUID id, ChallengeStatus status) {
        jpaRepository.updateStatus(id, status);
    }
}
