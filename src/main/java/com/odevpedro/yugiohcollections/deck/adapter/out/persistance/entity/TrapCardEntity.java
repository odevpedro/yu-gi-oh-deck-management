package com.odevpedro.yugiohcollections.deck.adapter.out.persistance.entity;

import com.odevpedro.yugiohcollections.deck.domain.model.enums.TrapType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trap_cards")
@Getter
@Setter
@NoArgsConstructor
public class TrapCardEntity extends CardJpaEntity {

    @Enumerated(EnumType.STRING)
    private TrapType trapType;

    @Column(name = "owner_id")
    private String ownerId;
}