package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardSearchPort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardSearchPort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/external/cards")
public class YgoprodeckController {

    private final CardSearchPort cardSearchPort;

    public YgoprodeckController(CardSearchPort cardSearchPort) {
        this.cardSearchPort = cardSearchPort;
    }

    /**
     * Exemplos:
     *  - /external/cards/search?name=Dark%20Magician
     *  - /external/cards/search?fname=magician
     *  - /external/cards/search?type=SPELL
     *  - /external/cards/search?type=SPELL&race=Equip
     *  - /external/cards/search?type=SPELL&page=0&size=5
     */
    @GetMapping("/search")
    public ResponseEntity<List<Card>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String fname,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String race,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        // sanitize simples
        name = sanitize(name);
        fname = sanitize(fname);
        type = sanitize(type);
        race = sanitize(race);

        List<Card> result;

        if (hasText(name)) {
            result = cardSearchPort.searchByName(name);
            return ResponseEntity.ok(slice(result, page, size));
        }

        if (hasText(fname)) {
            result = cardSearchPort.searchByFuzzyName(fname);
            return ResponseEntity.ok(slice(result, page, size));
        }

        if (hasText(type)) {
            if (hasText(race)) {
                result = cardSearchPort.searchByTypeAndRace(type, race);
            } else {
                result = cardSearchPort.searchByType(type);
            }
            return ResponseEntity.ok(slice(result, page, size));
        }

        return ResponseEntity.badRequest().build();
    }

    /* ===== helpers ===== */

    private boolean hasText(String s) { return StringUtils.hasText(s); }

    private String sanitize(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("^\"|\"$", "");
    }

    /** Corta a lista para simular paginação: page começa em 0. */
    private <T> List<T> slice(List<T> all, int page, int size) {
        int p = Math.max(page, 0);
        int s = Math.max(size, 1);
        int from = Math.min(p * s, all.size());
        int to   = Math.min(from + s, all.size());
        return all.subList(from, to);
    }
}