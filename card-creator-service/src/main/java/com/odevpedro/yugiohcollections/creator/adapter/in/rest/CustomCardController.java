package com.odevpedro.yugiohcollections.creator.adapter.in.rest;

import com.odevpedro.yugiohcollections.creator.application.dto.CreateCardRequest;
import com.odevpedro.yugiohcollections.creator.application.service.CustomCardService;
import com.odevpedro.yugiohcollections.creator.domain.model.CustomCard;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/custom-cards")
@RequiredArgsConstructor
public class CustomCardController {

    private final CustomCardService service;

    @PostMapping
    public ResponseEntity<CustomCard> create(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreateCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomCard> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomCard>> findByOwner(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(service.findAllByOwner(userId));
    }
}