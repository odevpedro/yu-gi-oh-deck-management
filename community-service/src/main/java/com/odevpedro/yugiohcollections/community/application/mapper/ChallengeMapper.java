package com.odevpedro.yugiohcollections.community.application.mapper;

import com.odevpedro.yugiohcollections.community.adapter.out.persistence.entity.ChallengeEntity;
import com.odevpedro.yugiohcollections.community.domain.model.Challenge;
import org.springframework.stereotype.Component;

@Component
public class ChallengeMapper {

    public ChallengeEntity toEntity(Challenge challenge) {
        return ChallengeEntity.builder()
                .id(challenge.getId())
                .challengerId(challenge.getChallengerId())
                .targetId(challenge.getTargetId())
                .challengerDeckId(challenge.getChallengerDeckId())
                .message(challenge.getMessage())
                .status(challenge.getStatus())
                .createdAt(challenge.getCreatedAt())
                .expiresAt(challenge.getExpiresAt())
                .build();
    }

    public Challenge toDomain(ChallengeEntity entity) {
        return Challenge.builder()
                .id(entity.getId())
                .challengerId(entity.getChallengerId())
                .targetId(entity.getTargetId())
                .challengerDeckId(entity.getChallengerDeckId())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }
}
