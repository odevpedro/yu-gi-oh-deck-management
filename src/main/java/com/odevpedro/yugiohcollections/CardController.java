package com.odevpedro.yugiohcollections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odevpedro.yugiohcollections.domain.model.Card;
import com.odevpedro.yugiohcollections.response.CardForm;
import com.odevpedro.yugiohcollections.service.CardService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final String YGO_URL = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name=";
    private final CardService cardService;

    private final  RestTemplate restTemplate;

    public CardController(CardService cardService, RestTemplate restTemplate, RestTemplate restTemplate1) {
        this.cardService = cardService;
        this.restTemplate = restTemplate1;
    }

    @GetMapping
    public List<Card> getAll() {
        return cardService.findAll();
    }

    @PostMapping
    public ResponseEntity<Object> addCarta(@PathVariable String name) throws JsonProcessingException {

        ResponseEntity<String> response = restTemplate.getForEntity(YGO_URL+name, String.class);
        String json = response.getBody();

        // Analisa a resposta da API e cria uma nova instância de Carta com as informações relevantes
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode data = root.path("data");
        String cardName = data.findPath("name").asText();
        String attribute = data.findPath("attribute").asText();
        String effect = data.findPath("archetype").asText();
        String type = data.findPath("type").asText();

        System.out.println(attribute);
        Card card = new Card();
        card.setName(cardName);
        card.setType(type);
        card.setAtribtue(attribute);
        card.setEffect(effect);
        System.out.println(card);
        return ResponseEntity.status(HttpStatus.OK).body(cardService.save(card));
    }


    @GetMapping("/{name}")
    public ResponseEntity<Object> getByName(@PathVariable String name) throws JsonProcessingException {
        // Busca a carta na API do Yu-Gi-Oh
        String url = "https://db.ygoprodeck.com/api/v7/cardinfo.php?name=" + name;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String json = response.getBody();

        // Analisa a resposta da API e cria uma nova instância de Carta com as informações relevantes
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode data = root.path("data");
        String cardName = data.findPath("name").asText();
        String attribute = data.findPath("attribute").asText();
        String effect = data.findPath("archetype").asText();
        String type = data.findPath("type").asText();

        System.out.println(attribute);
        Card card = new Card();
        card.setName(cardName);
        card.setType(type);
        card.setAtribtue(attribute);
        card.setEffect(effect);
        System.out.println(card);
        card.setId(1L);
        return ResponseEntity.status(HttpStatus.OK).body(cardService.save(card));
    }

    //Criando carta inexistente
    @PostMapping("/create")
    public ResponseEntity<Object> createNewCard(@PathVariable CardForm cardForm) {
        Card card = new Card();
        BeanUtils.copyProperties(cardForm, card);
        return ResponseEntity.status(HttpStatus.OK).body(cardService.save(card));
    }


}
