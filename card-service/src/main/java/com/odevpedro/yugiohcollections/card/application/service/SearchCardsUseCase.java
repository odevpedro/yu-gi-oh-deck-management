package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.adapter.out.dto.CardResponseDTO;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardPersistencePort;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardQueryPort;
import com.odevpedro.yugiohcollections.card.domain.model.ports.ExternalCardQueryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCardsUseCase {

    private final CardQueryPort cardQueryPort;             // DB
    private final ExternalCardQueryPort externalQueryPort; // Feign
    private final CardPersistencePort cardPersistence;     // cache/persist (upsert)

    public SearchCardsUseCase(CardQueryPort cardQueryPort,
                              ExternalCardQueryPort externalQueryPort,
                              CardPersistencePort cardPersistence) {
        this.cardQueryPort = cardQueryPort;
        this.externalQueryPort = externalQueryPort;
        this.cardPersistence = cardPersistence;
    }

    public Page<CardResponseDTO> search(String name, String fname, String type, Pageable pageable) {
        name  = sanitize(name);
        fname = sanitize(fname);
        type  = sanitize(type);

        // 1) Busca por nome (externo)
        if (hasText(name) || hasText(fname)) {
            List<Card> found;
            try {
                found = hasText(name)
                        ? externalQueryPort.findByExactName(name)
                        : externalQueryPort.findByFuzzyName(fname);
            } catch (Exception e) {
                // Resiliência: externo falhou -> retorna vazio
                found = List.of();
            }

            // Cache quente (UPsert por id)
            for (Card c : found) {
                try { cardPersistence.save(c); } catch (Exception ignore) {}
            }

            List<CardResponseDTO> dtos = found.stream().map(this::toDto).toList();
            return toPage(dtos, pageable);
        }

        // 2) Filtro por tipo (DB)
        if (hasText(type)) {
            CardType cardType = toCardType(type);
            if (cardType == null) {
                // sua escolha: Page.empty(...) ou lançar exceção 400
                return Page.empty(pageable);
            }
            return cardQueryPort.findAllByType(cardType, pageable)
                    .map(this::toDto);
        }

        // 3) Sem critérios -> vazio (ou lance 400)
        return Page.empty(pageable);
    }

    /* helpers */

    private String sanitize(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("^\"|\"$", "");
    }
    private boolean hasText(String s){ return s != null && !s.isBlank(); }

    private CardType toCardType(String raw) {
        if (raw == null) return null;
        String v = raw.trim().toUpperCase();
        try {
            return CardType.valueOf(v); // espera MONSTER|SPELL|TRAP
        } catch (IllegalArgumentException ex) {
            // tolerar variantes comuns
            if (v.contains("SPELL")) return CardType.SPELL;
            if (v.contains("TRAP"))  return CardType.TRAP;
            if (v.contains("MONSTER")) return CardType.MONSTER;
            return null;
        }
    }

    private CardResponseDTO toDto(Card c) {
        return new CardResponseDTO(c.getId(), c.getName(), c.getType().name(), c.getImageUrl());
    }

    private Page<CardResponseDTO> toPage(List<CardResponseDTO> list, Pageable p) {
        int start = Math.min((int) p.getOffset(), list.size());
        int end   = Math.min(start + p.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), p, list.size());
    }
}
