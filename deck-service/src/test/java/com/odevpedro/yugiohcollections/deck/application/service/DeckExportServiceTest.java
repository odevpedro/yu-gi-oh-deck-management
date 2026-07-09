package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckCardEntryEntity;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckEntity;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.repository.DeckJpaRepository;
import com.odevpedro.yugiohcollections.deck.application.mapper.DeckMapper;
import com.odevpedro.yugiohcollections.deck.application.service.Impl.DeckExportServiceImpl;
import com.odevpedro.yugiohcollections.deck.domain.model.DeckZone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeckExportServiceImpl — exportacao .ydk")
class DeckExportServiceTest {

    @Mock
    private DeckJpaRepository deckJpaRepository;

    private final DeckMapper deckMapper = new DeckMapper();

    @Test
    void shouldExportDeckAsYdkText() {
        DeckEntity entity = new DeckEntity();
        entity.setId(10L);
        entity.setOwnerId("user-1");
        entity.setName("Dragon Deck");
        entity.setEntries(new ArrayList<>());
        entity.addEntry(entry(1L, 1, DeckZone.MAIN));
        entity.addEntry(entry(2L, 1, DeckZone.MAIN));
        entity.addEntry(entry(3L, 1, DeckZone.EXTRA));
        entity.addEntry(entry(4L, 1, DeckZone.SIDE));

        when(deckJpaRepository.findByOwnerIdAndId("user-1", 10L)).thenReturn(Optional.of(entity));
        DeckExportServiceImpl service = new DeckExportServiceImpl(deckJpaRepository, deckMapper);

        String ydk = service.exportAsYdk(10L, "user-1");

        assertThat(ydk).isEqualTo("#main\n1\n2\n#extra\n3\n!side\n4\n");
    }

    @Test
    void shouldFailWhenDeckDoesNotExist() {
        when(deckJpaRepository.findByOwnerIdAndId("user-1", 10L)).thenReturn(Optional.empty());
        DeckExportServiceImpl service = new DeckExportServiceImpl(deckJpaRepository, deckMapper);

        assertThatThrownBy(() -> service.exportAsYdk(10L, "user-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Deck não encontrado");
    }

    private DeckCardEntryEntity entry(Long cardId, int quantity, DeckZone zone) {
        DeckCardEntryEntity entry = new DeckCardEntryEntity();
        entry.setCardId(cardId);
        entry.setQuantity(quantity);
        entry.setZone(zone);
        return entry;
    }
}
