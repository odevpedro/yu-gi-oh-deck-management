package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.adapter.out.dto.CardResponseDTO;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.card.domain.model.SpellCard;
import com.odevpedro.yugiohcollections.card.domain.model.ports.ExternalCardQueryPort;
import com.odevpedro.yugiohcollections.shared.enums.CardType;
import com.odevpedro.yugiohcollections.shared.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.shared.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.shared.enums.MonsterType;
import com.odevpedro.yugiohcollections.shared.enums.SpellType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchCardsUseCase — consultas de catalogo")
class SearchCardsUseCaseTest {

    @Mock
    private ExternalCardQueryPort externalQueryPort;

    @Test
    void shouldSearchByExactName() {
        SearchCardsUseCase useCase = new SearchCardsUseCase(externalQueryPort);
        Card card = monster(1L, "Blue-Eyes White Dragon");
        when(externalQueryPort.findByExactName("Blue-Eyes White Dragon")).thenReturn(List.of(card));

        var page = useCase.search("Blue-Eyes White Dragon", null, null, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Blue-Eyes White Dragon");
    }

    @Test
    void shouldFilterFuzzySearchByType() {
        SearchCardsUseCase useCase = new SearchCardsUseCase(externalQueryPort);
        when(externalQueryPort.findByFuzzyName("Dragon")).thenReturn(List.of(
                monster(1L, "Blue-Eyes White Dragon"),
                spell(2L, "Dragon Shield")
        ));

        var page = useCase.search(null, "Dragon", "monster", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getType()).isEqualTo("MONSTER");
    }

    @Test
    void shouldMapSearchByIds() {
        SearchCardsUseCase useCase = new SearchCardsUseCase(externalQueryPort);
        when(externalQueryPort.findByIds(List.of(1L))).thenReturn(List.of(monster(1L, "Blue-Eyes White Dragon")));

        List<com.odevpedro.yugiohcollections.card.application.dto.CardSummaryDTO> result = useCase.searchByIds(List.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Blue-Eyes White Dragon");
    }

    private Card monster(Long id, String name) {
        return new MonsterCard(
                id,
                name,
                "desc",
                "archetype",
                CardType.MONSTER,
                "img",
                3000,
                2500,
                8,
                MonsterAttribute.LIGHT,
                MonsterType.DRAGON,
                Set.of(MonsterSubType.NORMAL),
                "owner"
        );
    }

    private Card spell(Long id, String name) {
        return new SpellCard(
                id,
                name,
                "desc",
                "archetype",
                CardType.SPELL,
                "img",
                SpellType.QUICK_PLAY,
                "owner"
        );
    }
}
