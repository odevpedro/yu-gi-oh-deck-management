package com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "decks")
@Getter
@Setter
@NoArgsConstructor
public class DeckEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "owner_id")
    private String ownerId;

    @ElementCollection
    @CollectionTable(name = "deck_entity_main_deck", joinColumns = @JoinColumn(name = "deck_entity_id"))
    @Column(name = "main_deck")
    private List<Long> mainDeck;

    @ElementCollection
    @CollectionTable(name = "deck_entity_extra_deck", joinColumns = @JoinColumn(name = "deck_entity_id"))
    @Column(name = "extra_deck")
    private List<Long> extraDeck;

    @ElementCollection
    @CollectionTable(name = "deck_entity_side_deck", joinColumns = @JoinColumn(name = "deck_entity_id"))
    @Column(name = "side_deck")
    private List<Long> sideDeck;
}