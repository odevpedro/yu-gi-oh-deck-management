package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import com.odevpedro.yugiohcollections.card.application.service.AddToCollectionUseCase;
import com.odevpedro.yugiohcollections.card.application.service.ListCollectionUseCase;
import com.odevpedro.yugiohcollections.card.application.service.RemoveFromCollectionUseCase;
import com.odevpedro.yugiohcollections.card.application.service.UpdateEntryUseCase;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.UserCardView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/cards")
public class CollectionController {

    private final AddToCollectionUseCase addUC;
    private final ListCollectionUseCase listUC;
    private final UpdateEntryUseCase updateUC;
    private final RemoveFromCollectionUseCase removeUC;

    public CollectionController(AddToCollectionUseCase addUC, ListCollectionUseCase listUC,
                                UpdateEntryUseCase updateUC, RemoveFromCollectionUseCase removeUC) {
        this.addUC = addUC; this.listUC = listUC; this.updateUC = updateUC; this.removeUC = removeUC;
    }

    @GetMapping
    public ResponseEntity<List<UserCardView>> list(@PathVariable String userId) {
        return ResponseEntity.ok(listUC.list(userId));
    }

    public record AddRequest(CardType type, Long cardId, Integer quantity, String notes) {}
    @PostMapping
    public ResponseEntity<UserCardView> add(@PathVariable String userId, @RequestBody AddRequest req) {
        int qty = req.quantity() == null ? 1 : req.quantity();
        return ResponseEntity.ok(addUC.add(userId, req.type(), req.cardId(), qty, req.notes()));
    }

    public record UpdateRequest(Integer quantity, String notes) {}
    @PutMapping("/{entryId}")
    public ResponseEntity<UserCardView> update(@PathVariable String userId, @PathVariable Long entryId,
                                               @RequestBody UpdateRequest req) {
        int qty = req.quantity() == null ? 1 : req.quantity();
        return updateUC.update(userId, entryId, qty, req.notes())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> remove(@PathVariable String userId, @PathVariable Long entryId) {
        return removeUC.remove(userId, entryId) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
