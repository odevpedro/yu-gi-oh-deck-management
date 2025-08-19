package com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity;

import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "user_cards",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_card", columnNames = {"user_id", "card_type", "card_id"})
)
@Getter @Setter @NoArgsConstructor
public class UserCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name="card_type", nullable = false)
    private CardType cardType;

    @Column(name="card_id", nullable = false)
    private Long cardId;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column
    private String notes;
}

