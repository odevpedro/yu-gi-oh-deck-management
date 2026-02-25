package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaveCardUseCase — orquestração de criação de carta")
class SaveCardUseCaseTest {

    @Mock
    private CardPersistencePort persistencePort;

    private SaveCardUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SaveCardUseCase(persistencePort);
    }

    @Test
    @DisplayName("deve salvar carta quando DTO é válido")
    void shouldSaveCardWhenDtoIsValid() {
        CardInputDTO dto = validMonsterDTO();

        MonsterCard savedCard = new MonsterCard(
                1L, "Dark Magician", "The ultimate wizard", "Magician",
                CardType.MONSTER, "http://img.url", 2500, 2100, 7,
                MonsterAttribute.DARK, MonsterType.SPELLCASTER,
                Set.of(MonsterSubType.NORMAL), "user-1"
        );

        when(persistencePort.save(any(Card.class))).thenReturn(savedCard);

        Card result = useCase.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Dark Magician");
        verify(persistencePort, times(1)).save(any(Card.class));
    }

    @Test
    @DisplayName("deve lançar exceção quando tipo é nulo")
    void shouldThrowWhenTypeIsNull() {
        CardInputDTO dto = validMonsterDTO();
        dto.setType(null);

        assertThatThrownBy(() -> useCase.execute(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dados inválidos");

        verifyNoInteractions(persistencePort);
    }

    @Test
    @DisplayName("deve lançar exceção quando nome está em branco")
    void shouldThrowWhenNameIsBlank() {
        CardInputDTO dto = validMonsterDTO();
        dto.setName("  ");

        assertThatThrownBy(() -> useCase.execute(dto))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(persistencePort);
    }

    @Test
    @DisplayName("não deve chamar persistência quando o DTO é inválido")
    void shouldNotCallPersistenceWhenDtoIsInvalid() {
        CardInputDTO dto = new CardInputDTO(); // sem tipo e sem nome

        assertThatThrownBy(() -> useCase.execute(dto))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(persistencePort);
    }

    private CardInputDTO validMonsterDTO() {
        CardInputDTO dto = new CardInputDTO();
        dto.setType(CardType.MONSTER);
        dto.setName("Dark Magician");
        dto.setDescription("The ultimate wizard");
        dto.setArchetype("Magician");
        dto.setImageUrl("http://img.url");
        dto.setAttack(2500);
        dto.setDefense(2100);
        dto.setLevel(7);
        dto.setAttribute(MonsterAttribute.DARK);
        dto.setMonsterType(MonsterType.SPELLCASTER);
        dto.setSubTypes(Set.of(MonsterSubType.NORMAL));
        dto.setOwnerId("user-1");
        return dto;
    }
}