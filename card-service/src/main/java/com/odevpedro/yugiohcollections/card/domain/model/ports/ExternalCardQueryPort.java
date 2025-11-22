package com.odevpedro.yugiohcollections.card.domain.model.ports;

import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ExternalCardQueryPort {
    List<Card> findByExactName(String name);
    List<Card> findByFuzzyName(String fname);
    List<Card> findByIds(List<Long> ids);

    List<Card> findByType(CardType type);
    List<Card> findByTypeAndRace(CardType type, String race);
}