package com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "decks")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DeckEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeckCardEntryEntity> entries = new ArrayList<>();

    /** Helper para manter o vínculo bidirecional */
    public void addEntry(DeckCardEntryEntity entry) {
        entry.setDeck(this);
        this.entries.add(entry);
    }

    public void removeEntry(DeckCardEntryEntity entry) {
        this.entries.remove(entry);
        entry.setDeck(null);
}
}