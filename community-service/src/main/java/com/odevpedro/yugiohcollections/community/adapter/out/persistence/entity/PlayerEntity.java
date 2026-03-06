package com.odevpedro.yugiohcollections.community.adapter.out.persistence.entity;

import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private String displayName;

    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point location;

    @ElementCollection
    @CollectionTable(name = "player_platforms", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "platform")
    private List<String> platforms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DuelStatus duelStatus;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
