package com.odevpedro.yugiohcollections.creator.adapter.in.rest;

import com.odevpedro.yugiohcollections.creator.application.dto.CreateCardRequest;
import com.odevpedro.yugiohcollections.creator.application.service.CustomCardService;
import com.odevpedro.yugiohcollections.creator.domain.model.CustomCard;
import com.odevpedro.yugiohcollections.shared.constants.ApiRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiRoutes.CUSTOM_CARDS_BASE)
@RequiredArgsConstructor
public class CustomCardController {

    private final CustomCardService service;

    @PostMapping
    public ResponseEntity<CustomCard> create(
            Authentication auth,
            @RequestBody CreateCardRequest request) {
        String userId = (String) auth.getDetails();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(userId, request));
    }

    @GetMapping(ApiRoutes.CUSTOM_CARDS_BY_ID)
    public ResponseEntity<CustomCard> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomCard>> findByOwner(Authentication auth) {
        String userId = (String) auth.getDetails();
        return ResponseEntity.ok(service.findAllByOwner(userId));
    }
}