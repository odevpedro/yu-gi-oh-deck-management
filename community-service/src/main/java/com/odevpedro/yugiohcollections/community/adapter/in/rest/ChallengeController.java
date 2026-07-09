package com.odevpedro.yugiohcollections.community.adapter.in.rest;

import com.odevpedro.yugiohcollections.community.application.service.ChallengeService;
import com.odevpedro.yugiohcollections.community.domain.model.Challenge;
import com.odevpedro.yugiohcollections.shared.constants.ApiRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.CHALLENGES_BASE)
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

    @PatchMapping(ApiRoutes.CHALLENGES_BY_ID)
    public ResponseEntity<RespondChallengeResponse> respond(Authentication auth,
                                                            @PathVariable UUID challengeId,
                                                            @RequestBody RespondChallengeRequest body) {
        UUID targetId = UUID.fromString((String) auth.getDetails());
        return switch (body.action()) {
            case ACCEPT -> {
                if (body.targetDeckId() == null) {
                    throw new IllegalArgumentException("targetDeckId is required when action is ACCEPT");
                }
                var accepted = challengeService.accept(challengeId, targetId, body.targetDeckId());
                yield ResponseEntity.ok(new RespondChallengeResponse(accepted.challenge(), accepted.duelId()));
            }
            case DECLINE -> ResponseEntity.ok(new RespondChallengeResponse(challengeService.decline(challengeId, targetId), null));
        };
    }

    @GetMapping(ApiRoutes.CHALLENGES_PENDING)
    public ResponseEntity<List<Challenge>> pending(Authentication auth) {
        UUID targetId = UUID.fromString((String) auth.getDetails());
        return ResponseEntity.ok(challengeService.findPending(targetId));
    }

    public record SendChallengeRequest(UUID targetId, Long challengerDeckId, String message) {}
    public record RespondChallengeRequest(ChallengeAction action, Long targetDeckId) {}
    public record RespondChallengeResponse(Challenge challenge, String duelId) {}
    public enum ChallengeAction { ACCEPT, DECLINE }
}
