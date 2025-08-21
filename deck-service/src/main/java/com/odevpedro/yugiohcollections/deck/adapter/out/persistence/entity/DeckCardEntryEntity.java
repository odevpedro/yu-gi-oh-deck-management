package com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity;

import com.odevpedro.yugiohcollections.deck.domain.model.DeckZone;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "deck_card_entry")
@Getter
@Setter
public class DeckCardEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com o Deck
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private DeckEntity deck;

    // Apenas referência ao ID da carta do card-service
    @Column(name = "card_id", nullable = false)
    private Long cardId;

    // Quantidade da carta no deck (1, 2 ou 3 geralmente no YGO)
    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeckZone zone;

}