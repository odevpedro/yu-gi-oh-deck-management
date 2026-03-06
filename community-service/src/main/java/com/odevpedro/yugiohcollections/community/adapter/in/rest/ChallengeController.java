package com.odevpedro.yugiohcollections.community.adapter.in.rest;

import com.odevpedro.yugiohcollections.community.application.service.ChallengeService;
import com.odevpedro.yugiohcollections.community.domain.model.Challenge;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<Challenge> send(Authentication auth,
                                          @RequestBody SendChallengeRequest body) {
        UUID challengerId = UUID.fromString((String) auth.getDetails());
        return ResponseEntity.ok(challengeService.sendChallenge(
                challengerId, body.targetId(), body.challengerDeckId(), body.message()));
    }

    @PatchMapping("/{challengeId}")
    public ResponseEntity<Challenge> respond(Authentication auth,
                                             @PathVariable UUID challengeId,
                                             @RequestBody RespondChallengeRequest body) {
        UUID targetId = UUID.fromString((String) auth.getDetails());
        return switch (body.action()) {
            case ACCEPT  -> ResponseEntity.ok(challengeService.accept(challengeId, targetId));
            case DECLINE -> ResponseEntity.ok(challengeService.decline(challengeId, targetId));
        };
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Challenge>> pending(Authentication auth) {
        UUID targetId = UUID.fromString((String) auth.getDetails());
        return ResponseEntity.ok(challengeService.findPending(targetId));
    }

    public record SendChallengeRequest(UUID targetId, Long challengerDeckId, String message) {}
    public record RespondChallengeRequest(ChallengeAction action) {}
    public enum ChallengeAction { ACCEPT, DECLINE }
}
