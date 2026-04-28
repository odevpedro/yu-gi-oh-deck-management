package com.odevpedro.yugiohcollections.community.adapter.in.rest;

import com.odevpedro.yugiohcollections.community.application.service.PlayerService;
import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import com.odevpedro.yugiohcollections.community.domain.model.Player;
import com.odevpedro.yugiohcollections.shared.constants.ApiRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.PLAYERS_BASE)
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<Player> registerOrUpdate(Authentication auth,
                                                   @RequestBody RegisterPlayerRequest body) {
        UUID userId = UUID.fromString((String) auth.getDetails());
        return ResponseEntity.ok(playerService.registerOrUpdate(
                userId, body.displayName(), body.latitude(), body.longitude(), body.platforms()));
    }

    @PatchMapping(ApiRoutes.PLAYERS_ME_STATUS)
    public ResponseEntity<Void> updateStatus(Authentication auth,
                                             @RequestBody UpdateStatusRequest body) {
        UUID userId = UUID.fromString((String) auth.getDetails());
        playerService.updateStatus(userId, body.status());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiRoutes.PLAYERS_NEARBY)
    public ResponseEntity<List<Player>> findNearby(@RequestParam double lat,
                                                   @RequestParam double lng,
                                                   @RequestParam double radiusKm,
                                                   @RequestParam(required = false) DuelStatus status) {
        return ResponseEntity.ok(playerService.findNearby(lat, lng, radiusKm, status));
    }

    public record RegisterPlayerRequest(String displayName, double latitude, double longitude, List<String> platforms) {}
    public record UpdateStatusRequest(DuelStatus status) {}
}
