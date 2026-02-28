package com.odevpedro.yugiohcollections.creator.adapter.out.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardValidatedEvent {
    private Long cardId;
    private String status;       // APPROVED | REJECTED
    private String rejectReason; // null se APPROVED
}