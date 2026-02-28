package com.odevpedro.yugiohcollections.card.domain;

import com.odevpedro.yugiohcollections.card.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.shared.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.shared.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.shared.enums.MonsterType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MonsterCard — regras de criação")
class MonsterCardTest {

    @Test
    @DisplayName("deve criar carta válida quando todos os campos estão corretos")
    void shouldCreateValidMonsterCard() {
        Optional<MonsterCard> card = MonsterCard.create(
                1L, "Dark Magician", "The ultimate wizard", "Magician",
                "http://img.url", 2500, 2100, 7,
                MonsterAttribute.DARK, MonsterType.SPELLCASTER,
                Set.of(MonsterSubType.NORMAL), "user-1"
        );

        assertThat(card).isPresent();
        assertThat(card.get().getName()).isEqualTo("Dark Magician");
        assertThat(card.get().getAttack()).isEqualTo(2500);
        assertThat(card.get().getLevel()).isEqualTo(7);
    }

    @Test
    @DisplayName("deve retornar vazio quando o nome é nulo")
    void shouldReturnEmptyWhenNameIsNull() {
        Optional<MonsterCard> card = MonsterCard.create(
                1L, null, "desc", null, "http://img.url",
                2500, 2100, 7,
                MonsterAttribute.DARK, MonsterType.SPELLCASTER,
                Set.of(MonsterSubType.NORMAL), "user-1"
        );

        assertThat(card).isEmpty();
    }

    @Test
    @DisplayName("deve retornar vazio quando o nome está em branco")
    void shouldReturnEmptyWhenNameIsBlank() {
        Optional<MonsterCard> card = MonsterCard.create(
                1L, "  ", "desc", null, "http://img.url",
                2500, 2100, 7,
                MonsterAttribute.DARK, MonsterType.SPELLCASTER,
                Set.of(MonsterSubType.NORMAL), "user-1"
        );

        assertThat(card).isEmpty();
    }

    @Test
    @DisplayName("deve retornar vazio quando o level é menor que 1")
    void shouldReturnEmptyWhenLevelIsZero() {
        Optional<MonsterCard> card = MonsterCard.create(
                1L, "Dark Magician", "desc", null, "http://img.url",
                2500, 2100, 0,
                MonsterAttribute.DARK, MonsterType.SPELLCASTER,
                Set.of(MonsterSubType.NORMAL), "user-1"
        );

        assertThat(card).isEmpty();
    }

    @Test
    @DisplayName("deve retornar vazio quando o level é maior que 12")
    void shouldReturnEmptyWhenLevelExceedsMax() {
        Optional<MonsterCard> card = MonsterCard.create(
                1L, "Dark Magician", "desc", null, "http://img.url",
                2500, 2100, 13,
                MonsterAttribute.DARK, MonsterType.SPELLCASTER,
                Set.of(MonsterSubType.NORMAL), "user-1"
        );

        assertThat(card).isEmpty();
    }

    @Test
    @DisplayName("deve retornar vazio quando attribute é nulo")
    void shouldReturnEmptyWhenAttributeIsNull() {
        Optional<MonsterCard> card = MonsterCard.create(
                1L, "Dark Magician", "desc", null, "http://img.url",
                2500, 2100, 7,
                null, MonsterType.SPELLCASTER,
                Set.of(MonsterSubType.NORMAL), "user-1"
        );

        assertThat(card).isEmpty();
    }

    @Test
    @DisplayName("deve aceitar level 1 como válido (limite inferior)")
    void shouldAcceptMinimumLevel() {
        Optional<MonsterCard> card = MonsterCard.create(
                1L, "Kuriboh", "desc", null, "http://img.url",
                300, 200, 1,
                MonsterAttribute.DARK, MonsterType.FIEND,
                Set.of(MonsterSubType.EFFECT), "user-1"
        );

        assertThat(card).isPresent();
        assertThat(card.get().getLevel()).isEqualTo(1);
    }

    @Test
    @DisplayName("deve aceitar level 12 como válido (limite superior)")
    void shouldAcceptMaximumLevel() {
        Optional<MonsterCard> card = MonsterCard.create(
                1L, "Exodia the Forbidden One", "desc", null, "http://img.url",
                1000, 1000, 12,
                MonsterAttribute.DARK, MonsterType.SPELLCASTER,
                Set.of(MonsterSubType.NORMAL), "user-1"
        );

        assertThat(card).isPresent();
    }
}