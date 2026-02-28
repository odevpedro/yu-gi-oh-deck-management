package com.odevpedro.yugiohcollections.creator.adapter.out.persistence.repository;

import com.odevpedro.yugiohcollections.creator.adapter.out.persistence.entity.CustomCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomCardJpaRepository extends JpaRepository<CustomCardEntity, Long> {
    List<CustomCardEntity> findAllByOwnerIdOrderByCreatedAtDesc(String ownerId);
}