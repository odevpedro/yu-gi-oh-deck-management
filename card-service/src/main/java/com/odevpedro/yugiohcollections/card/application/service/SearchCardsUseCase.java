package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.adapter.out.dto.CardResponseDTO;
import com.odevpedro.yugiohcollections.card.application.dto.CardSummaryDTO;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.ports.ExternalCardQueryPort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCardsUseCase {

    private final ExternalCardQueryPort externalQueryPort;

    public SearchCardsUseCase(ExternalCardQueryPort externalQueryPort) {
        this.externalQueryPort = externalQueryPort;
    }

    public Page<CardResponseDTO> search(String name, String fname, String type, Pageable pageable) {
        name  = sanitize(name);
        fname = sanitize(fname);
        type  = sanitize(type);

        if (hasText(name)) {
            return toPage(mapToDto(externalQueryPort.findByExactName(name)), pageable);
        }

        if (hasText(fname) && hasText(type)) {
            CardType target = toCardType(type);
            List<Card> list = externalQueryPort.findByFuzzyName(fname).stream()
                    .filter(c -> c.getType() == target)
                    .toList();
            return toPage(mapToDto(list), pageable);
        }

        if (hasText(fname)) {
            return toPage(mapToDto(externalQueryPort.findByFuzzyName(fname)), pageable);
        }

        if (hasText(type)) {
            CardType target = toCardType(type);
            if (target != null) {
                return toPage(mapToDto(externalQueryPort.findByType(target)), pageable);
            }
        }

        return Page.empty(pageable);
    }

    public List<CardSummaryDTO> searchByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return externalQueryPort.findByIds(ids).stream()
                .map(c -> new CardSummaryDTO(
                        c.getId(),
                        c.getName(),
                        c.getType().name(),
                        c.getImageUrl(),
                        c.getDescription()
                ))
                .toList();
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
            return CardType.valueOf(v);
        } catch (IllegalArgumentException ex) {
            if (v.contains("SPELL"))   return CardType.SPELL;
            if (v.contains("TRAP"))    return CardType.TRAP;
            if (v.contains("MONSTER")) return CardType.MONSTER;
            return null;
        }
    }

    private List<CardResponseDTO> mapToDto(List<Card> list) {
        return list.stream().map(this::toDto).toList();
    }

    private CardResponseDTO toDto(Card c) {
        return new CardResponseDTO(c.getId(), c.getName(), c.getType().name(), c.getImageUrl(), c.getDescription());
    }

    private Page<CardResponseDTO> toPage(List<CardResponseDTO> list, Pageable p) {
        int start = Math.min((int) p.getOffset(), list.size());
        int end   = Math.min(start + p.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), p, list.size());
    }
}