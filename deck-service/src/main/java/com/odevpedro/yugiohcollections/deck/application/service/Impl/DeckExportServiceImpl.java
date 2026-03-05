package com.odevpedro.yugiohcollections.deck.application.service.Impl;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.repository.DeckJpaRepository;
import com.odevpedro.yugiohcollections.deck.application.mapper.DeckMapper;
import com.odevpedro.yugiohcollections.deck.application.service.DeckExportService;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeckExportServiceImpl implements DeckExportService {

    private final DeckJpaRepository deckJpaRepository;
    private final DeckMapper deckMapper;


    @Override
    public String exportAsYdk(Long deckId, String ownerId) {
        Deck deck = deckJpaRepository.findByOwnerIdAndId(ownerId, deckId).map(deckMapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Deck não encontrado"));


        return buildSection("#main",  deck.getMainDeck())
                + buildSection("#extra", deck.getExtraDeck())
                + buildSection("!side",  deck.getSideDeck());
    }

    private String buildSection(String header, List<Long> cards) {
        if (cards == null || cards.isEmpty()) return header + "\n";
        return header + "\n"
                + cards.stream().map(String::valueOf).collect(Collectors.joining("\n"))
                + "\n";
    }

}
