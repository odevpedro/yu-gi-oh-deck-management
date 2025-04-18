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
    private String ownerId;

    @ElementCollection
    private List<Long> mainDeck;

    @ElementCollection
    private List<Long> extraDeck;

    @ElementCollection
    private List<Long> sideDeck;
}