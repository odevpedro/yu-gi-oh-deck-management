package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.DeckRepositoryAdapter;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardSummaryDTO;
import com.odevpedro.yugiohcollections.deck.adapter.out.messaging.DeckSyncEventPublisher;
import com.odevpedro.yugiohcollections.deck.application.service.Impl.DeckApplicationServiceImpl;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.service.DeckValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeckApplicationServiceImpl — orquestração de decks")
class DeckApplicationServiceImplTest {

    @Mock
    private DeckRepositoryAdapter deckRepository;

    @Mock
    private DeckCardCatalogService deckCardCatalogService;

    @Mock
    private DeckSyncEventPublisher deckSyncEventPublisher;

    private final DeckValidator deckValidator = new DeckValidator();
    private DeckApplicationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DeckApplicationServiceImpl(deckRepository, deckCardCatalogService, deckValidator, deckSyncEventPublisher);
    }

    @Test
    @DisplayName("deve criar deck e persistir via repository")
    void shouldCreateAndPersistDeck() {
        Deck expected = Deck.of("user-1", "Dragon Deck");
        when(deckRepository.save(any(Deck.class))).thenReturn(expected);

        Deck result = service.createDeck("user-1", "Dragon Deck");

        assertThat(result.getName()).isEqualTo("Dragon Deck");
        assertThat(result.getOwnerId()).isEqualTo("user-1");
        verify(deckRepository, times(1)).save(any(Deck.class));
        verify(deckSyncEventPublisher).publish(eq("CREATED"), any(Deck.class));
    }

    @Test
    @DisplayName("deve retornar todos os decks do owner")
    void shouldListDecksByOwner() {
        List<Deck> decks = List.of(
                Deck.of("user-1", "Dragon Deck"),
                Deck.of("user-1", "Spellcaster Deck")
        );
        when(deckRepository.findAllByOwnerId("user-1")).thenReturn(decks);

        List<Deck> result = service.listDecks("user-1");

        assertThat(result).hasSize(2);
        verify(deckRepository).findAllByOwnerId("user-1");
    }

    @Test
    @DisplayName("deve lançar exceção quando deck não existe para o owner")
    void shouldThrowWhenDeckNotFoundForOwner() {
        when(deckRepository.findByIdAndOwnerId(99L, "user-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDeck("user-1", 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Deck nao encontrado");
    }

    @Test
    @DisplayName("deve adicionar carta ao deck com quantidade correta")
    void shouldAddCardWithCorrectQuantity() {
        Deck deck = Deck.of("user-1", "Dragon Deck");
        when(deckRepository.findByIdAndOwnerId(1L, "user-1")).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(inv -> inv.getArgument(0));

        Deck result = service.addCard("user-1", 1L, 1001L, 3);

        assertThat(result.getMainDeck()).hasSize(3).containsOnly(1001L);
        verify(deckRepository).save(deck);
    }

    @Test
    @DisplayName("deve lançar exceção quando quantidade for zero ou negativa")
    void shouldThrowWhenQuantityIsZeroOrNegative() {
        assertThatThrownBy(() -> service.addCard("user-1", 1L, 1001L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser > 0");

        assertThatThrownBy(() -> service.addCard("user-1", 1L, 1001L, -1))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(deckRepository);
    }

    @Test
    @DisplayName("não deve chamar o Feign ao criar um deck")
    void shouldNotCallFeignOnDeckCreation() {
        when(deckRepository.save(any())).thenReturn(Deck.of("user-1", "Test"));

        service.createDeck("user-1", "Test");

        verifyNoInteractions(deckCardCatalogService);
    }


    @Test
    @DisplayName("deve manter o deck acessível quando o catálogo de cartas falha")
    void shouldReturnDeckWithoutCardDetailsWhenCatalogFails() {
        Deck deck = Deck.of("user-1", "Dragon Deck");
        deck.addToMain(1001L);
        when(deckRepository.findByIdAndOwnerId(1L, "user-1")).thenReturn(Optional.of(deck));
        when(deckCardCatalogService.loadCardInfo(anyList(), anyMap())).thenReturn(java.util.Map.of());

        var result = service.getDeckWithCards("user-1", 1L);

        assertThat(result.getCards()).hasSize(1);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getValidationErrors()).isNotEmpty();
    }

    @Test
    @DisplayName("deve importar deck .ydk, validar cartas e persistir")
    void shouldImportYdkDeckAndPersistIt() {
        StringBuilder ydk = new StringBuilder();
        ydk.append("#main\n");
        for (long i = 1001; i <= 1040; i++) {
            ydk.append(i).append('\n');
        }
        ydk.append("#extra\n");
        ydk.append("2001\n");
        ydk.append("2002\n");
        ydk.append("!side\n");
        ydk.append("3001\n");

        var file = new org.springframework.mock.web.MockMultipartFile(
                "file",
                "imported.ydk",
                "text/plain",
                ydk.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        when(deckRepository.save(any(Deck.class))).thenAnswer(inv -> inv.getArgument(0));
        when(deckCardCatalogService.loadCardInfo(anyList(), anyMap())).thenAnswer(inv -> {
            List<Long> ids = inv.getArgument(0);
            return ids.stream().collect(java.util.stream.Collectors.toMap(
                    id -> id,
                    id -> CardSummaryDTO.builder()
                            .cardId(id)
                            .name("Card " + id)
                            .build()
            ));
        });

        var result = service.importDeck("user-1", file);

        assertThat(result.getName()).isEqualTo("imported");
        assertThat(result.isValid()).isTrue();
        assertThat(result.getValidationErrors()).isEmpty();
        assertThat(result.getMainDeckSize()).isEqualTo(40);
        assertThat(result.getExtraDeckSize()).isEqualTo(2);
        assertThat(result.getSideDeckSize()).isEqualTo(1);
        verify(deckRepository, times(1)).save(any(Deck.class));
        verify(deckSyncEventPublisher).publish(eq("IMPORTED"), any(Deck.class));
    }
}
