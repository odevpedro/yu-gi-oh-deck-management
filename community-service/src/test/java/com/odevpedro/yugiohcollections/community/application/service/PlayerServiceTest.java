package com.odevpedro.yugiohcollections.community.application.service;

import com.odevpedro.yugiohcollections.community.application.service.Impl.PlayerServiceImpl;
import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import com.odevpedro.yugiohcollections.community.domain.model.Player;
import com.odevpedro.yugiohcollections.community.domain.port.PlayerRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlayerServiceImpl — gerenciamento de jogadores")
class PlayerServiceTest {

    @Mock
    private PlayerRepositoryPort playerRepository;

    private PlayerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PlayerServiceImpl(playerRepository);
    }

    @Test
    void shouldRegisterNewPlayerWhenAbsent() {
        UUID userId = UUID.randomUUID();
        when(playerRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));

        Player result = service.registerOrUpdate(userId, "Yugi", -23.5, -46.6, List.of("PC"));

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getDuelStatus()).isEqualTo(DuelStatus.AVAILABLE);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void shouldPreserveStatusWhenUpdatingExistingPlayer() {
        UUID userId = UUID.randomUUID();
        Player existing = Player.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .displayName("Old")
                .latitude(0.0)
                .longitude(0.0)
                .platforms(List.of("PC"))
                .duelStatus(DuelStatus.IN_DUEL)
                .build();

        when(playerRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));

        Player result = service.registerOrUpdate(userId, "Yugi", -23.5, -46.6, List.of("PC", "Mobile"));

        assertThat(result.getDuelStatus()).isEqualTo(DuelStatus.IN_DUEL);
        assertThat(result.getDisplayName()).isEqualTo("Yugi");
    }

    @Test
    void shouldUpdateStatusAfterValidation() {
        UUID userId = UUID.randomUUID();
        when(playerRepository.findByUserId(userId)).thenReturn(Optional.of(Player.of(userId, "Yugi", 0.0, 0.0, List.of("PC"))));

        service.updateStatus(userId, DuelStatus.LOOKING_FOR_DUEL);

        verify(playerRepository).updateStatus(userId, DuelStatus.LOOKING_FOR_DUEL);
    }
}
