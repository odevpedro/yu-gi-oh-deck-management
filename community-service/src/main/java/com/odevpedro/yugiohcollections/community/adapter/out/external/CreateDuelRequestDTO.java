package com.odevpedro.yugiohcollections.community.adapter.out.external;

public record CreateDuelRequestDTO(
        String playerAId,
        String playerBId,
        Long playerADeckId,
        Long playerBDeckId
) {}
