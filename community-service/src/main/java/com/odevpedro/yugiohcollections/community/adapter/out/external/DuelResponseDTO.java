package com.odevpedro.yugiohcollections.community.adapter.out.external;

public record DuelResponseDTO(
        String duelId,
        String playerAId,
        String playerBId,
        String currentPhase,
        String status,
        String winnerId,
        int turnNumber,
        String activePlayerId
) {}
