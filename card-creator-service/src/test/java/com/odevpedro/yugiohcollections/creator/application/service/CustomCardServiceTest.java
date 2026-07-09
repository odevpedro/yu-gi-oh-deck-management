package com.odevpedro.yugiohcollections.creator.application.service;

import com.odevpedro.yugiohcollections.creator.adapter.out.messaging.CardEventPublisher;
import com.odevpedro.yugiohcollections.creator.application.dto.CreateCardRequest;
import com.odevpedro.yugiohcollections.creator.domain.model.CustomCard;
import com.odevpedro.yugiohcollections.creator.domain.model.ports.CustomCardRepositoryPort;
import com.odevpedro.yugiohcollections.shared.enums.CardType;
import com.odevpedro.yugiohcollections.shared.enums.MonsterAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomCardServiceTest {

    @Mock
    private CustomCardRepositoryPort repository;

    @Mock
    private CardEventPublisher eventPublisher;

    private CustomCardService service;

    @BeforeEach
    void setUp() {
        service = new CustomCardService(repository, eventPublisher);
    }

    @Test
    void shouldCreateMonsterCardAndPublishEvent() {
        CreateCardRequest request = new CreateCardRequest(
                CardType.MONSTER.name(),
                "Blue-Eyes",
                "Legendary dragon",
                3000,
                2500,
                8,
                MonsterAttribute.LIGHT,
                "Dragon",
                "Normal summon",
                null
        );
        CustomCard saved = CustomCard.createMonster(
                "owner-1",
                "Blue-Eyes",
                "Legendary dragon",
                3000,
                2500,
                8,
                MonsterAttribute.LIGHT,
                "Dragon",
                "Normal summon"
        );
        when(repository.save(any(CustomCard.class))).thenReturn(saved);

        CustomCard result = service.create("owner-1", request);

        assertThat(result.getName()).isEqualTo("Blue-Eyes");
        verify(eventPublisher).publishCardCreated(any(CustomCard.class));
    }
}
