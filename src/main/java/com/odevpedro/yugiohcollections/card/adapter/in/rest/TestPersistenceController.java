package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import com.odevpedro.yugiohcollections.card.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterType;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardPersistencePort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;


@RestController
@RequestMapping("/test-persist")
public class TestPersistenceController {

    private final CardPersistencePort persistencePort;

    public TestPersistenceController(CardPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @PostMapping("/monster")
    public ResponseEntity<?> salvarCartaMonstro() {
        MonsterCard carta = new MonsterCard(
                99999999L,
                "Teste Dragão Sombrio",
                "Este é um monstro fictício para testar persistência",
                "Dark Test Archetype",
                CardType.MONSTER,
                "https://imagem.fake.com/dragao.png",
                2500,
                2100,
                7,
                MonsterAttribute.DARK,
                MonsterType.DRAGON,
                Set.of(MonsterSubType.NORMAL, MonsterSubType.EFFECT),
                "user-teste"
        );

        var saved = persistencePort.save(carta);
        return ResponseEntity.ok(saved);
    }
}