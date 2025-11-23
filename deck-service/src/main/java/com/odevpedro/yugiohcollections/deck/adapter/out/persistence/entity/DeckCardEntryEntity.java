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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private DeckEntity deck;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeckZone zone;

}