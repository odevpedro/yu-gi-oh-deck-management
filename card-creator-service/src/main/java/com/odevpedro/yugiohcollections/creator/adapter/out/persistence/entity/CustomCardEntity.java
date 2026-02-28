package com.odevpedro.yugiohcollections.creator.adapter.out.persistence.entity;

import com.odevpedro.yugiohcollections.creator.domain.model.enums.CardStatus;
import com.odevpedro.yugiohcollections.shared.enums.CardType;
import com.odevpedro.yugiohcollections.shared.enums.MonsterAttribute;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "custom_cards")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CustomCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;

    private String rejectReason;

    // Monstro
    private Integer attack;
    private Integer defense;
    private Integer level;

    @Enumerated(EnumType.STRING)
    private MonsterAttribute attribute;

    private String monsterType;
    private String summonCondition;

    // Spell / Trap
    private String subType;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}