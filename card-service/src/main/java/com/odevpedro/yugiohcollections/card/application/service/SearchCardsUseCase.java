package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.adapter.out.dto.CardResponseDTO;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardPersistencePort;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardQueryPort;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardSearchPort;
import com.odevpedro.yugiohcollections.card.domain.model.ports.ExternalCardQueryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCardsUseCase {

    private final CardQueryPort cardQueryPort;
    private final ExternalCardQueryPort externalQueryPort;
    private final CardPersistencePort cardPersistence;
    private final CardSearchPort cardSearchPort;

    public SearchCardsUseCase(CardQueryPort cardQueryPort,
                              ExternalCardQueryPort externalQueryPort,
                              CardPersistencePort cardPersistence,
                              CardSearchPort cardSearchPort) {
        this.cardQueryPort = cardQueryPort;
        this.externalQueryPort = externalQueryPort;
        this.cardPersistence = cardPersistence;
        this.cardSearchPort = cardSearchPort;
    }

    public Page<CardResponseDTO> search(String name, String fname, String type, Pageable pageable) {
        name  = sanitize(name);
        fname = sanitize(fname);
        type  = sanitize(type);

        if (hasText(name)) {
            List<Card> list = cardSearchPort.searchByName(name);
            return toPage(mapToDto(list), pageable);
        }

        if (hasText(fname) && hasText(type)) {
            var list = cardSearchPort.searchByFuzzyName(fname);
            var target = toCardType(type);
            list = list.stream()
                    .filter(c -> c.getType() == target)
                    .toList();
            return toPage(list.stream().map(this::toDto).toList(), pageable);
        }

        if (hasText(fname)) {
            List<Card> list = cardSearchPort.searchByFuzzyName(fname);
            return toPage(mapToDto(list), pageable);
        }


        if (hasText(type)) {
            List<Card> list = cardSearchPort.searchByType(type);
            return toPage(mapToDto(list), pageable);
        }

        return Page.empty(pageable);
    }


    private String sanitize(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("^\"|\"$", "");
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private CardType toCardType(String raw) {
        if (raw == null) return null;
        String v = raw.trim().toUpperCase();
        try {
            return CardType.valueOf(v); // MONSTER | SPELL | TRAP
        } catch (IllegalArgumentException ex) {
            if (v.contains("SPELL")) return CardType.SPELL;
            if (v.contains("TRAP"))  return CardType.TRAP;
            if (v.contains("MONSTER")) return CardType.MONSTER;
            return null;
        }
    }

    private List<CardResponseDTO> mapToDto(List<Card> list) {
        return list.stream()
                .map(this::toDto)
                .toList();
    }

    private CardResponseDTO toDto(Card c) {
        return new CardResponseDTO(
                c.getId(),
                c.getName(),
                c.getType().name(),
                c.getImageUrl()
        );
    }

    private Page<CardResponseDTO> toPage(List<CardResponseDTO> list, Pageable p) {
        int start = Math.min((int) p.getOffset(), list.size());
        int end   = Math.min(start + p.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), p, list.size());
    }
}
