_Este documento demonstra de forma prática a aplicação do padrão arquitetural Ports and Adapters(Hexagonal) na funcionalidade de busca de cartas do jogo Yu-Gi-Oh usando a API pública [YGOProDeck](https://ygoprodeck.com/api-guide/). Seguindo as boas práticas especificadas pela arquitetura em questão: separando as responsabilidades entre domínio, entrada, saída e infraestrutura externa._

---

## Introdução & Objetivos iniciais do projeto

Neste primeiro artigo venho apresentar a organização do projeto e como foi feita a modelagem do sistema. Primeiramente, vamos entender o domínio que está sendo abordado. Nas demais postagens será apresentado os outros micro serviços que compõem a aplicação e a comunicação entre eles através da mensageria.

Yu-Gi-Oh! é um card game estratégico, onde jogadores constroem e enfrentam decks compostos por diferentes tipos de cartas: monstros, mágicas, armadilhas, fusões, entre outras. O jogo possui regras bem estabelecidas e possui uma variedade enorme de cartas, com diferentes efeitos, atributos e classificações.

Este é um projeto feito na intenção de praticar diversos conceitos relevantes da linguagem java que são constantemente exigidos pelo mercado, nele levo em considerações temas como DDD, padrões de projetos, arquitetura de micro serviços e mensageria. O projeto está sendo feito usando o framework Spring Boot e tem como principal objetivo ser uma ferramenta de apoio para organização de decks, permitindo que o usuário:

> 1. Consulte cartas diretamente da API pública YGOProDeck
> 2. Crie e salve cartas customizadas
> 3. Monte decks personalizados, separados em Main Deck, Side Deck e Extra Deck
> 4. Valide cartas customizadas por meio de um microserviço de integridade (KonamiService) 
> 5. Visualize estatísticas e receba notificações futuras (em evolução)
> 6. Exporte seus decks no formado ydk.


&nbsp;





## Modelagem da Solução

A modelagem se deu da seguinte maneira: basicamente o game é formado por um número relativamente pequeno de cartas "principais" que vão servir de base para uma série subdivisões de cartas semelhantes. Por exemplo:

| ![](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/f9l5y296vx4d4xff68dq.png ) | ![](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/8ahiytq0qrpftz5osceb.png) | ![](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/3otcw4t5t3ifajmphhrg.png) |
|:--:|:--:|:--:|
| Monstro | Mágica | Armadilha |



| ![](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/qxu1oen0rqj6ptcakunf.png) | ![](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/0yd62dk0shykrf8zvlvr.png) |
|:--:|:--:|
| Modelo abstrato card | classe Java |

Considerando as chamadas de API feitas para cada um dos tipos exibidos acima, foi possivel identificar elementos em comum. Levando isso em consideração surge então a ideia de abstrair o que é compartilhado entre essas cartas em uma estrutura única. Dentro do contexto de orientação a objetos essa abordagem é conhecida como herança e foi aplicada através de uma classe abstrata chamada Card.java 


No artigo de hoje será desenvolvida a funcionalidade de busca externa de cartas usando OpenFeing como cliente que conta com a seguinte solução:

## 1. Fluxo da Requisição


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/85yejjxv6vmfo8aww1s1.png)


&nbsp;
## 2. Endpoint REST - `ExternalCardController`

```java
@RestController
@RequestMapping("/cards")
public class ExternalCardController {

    private final CardSearchPort cardSearchPort;

    public ExternalCardController(CardSearchPort cardSearchPort) {
        this.cardSearchPort = cardSearchPort;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Card>> searchByName(@RequestParam String name) {
        List<Card> cards = cardSearchPort.searchByName(name);
        return ResponseEntity.ok(cards);
    }
}
```

O controller expõe o endpoint `/cards/search` e injeta a interface `CardSearchPort`, que é uma porta do domínio. Ele não sabe como a busca é feita — só encaminha a requisição e retorna o resultado.

---

## 3. Porta - `CardSearchPort`

```java
public interface CardSearchPort {
    List<Card> searchByName(String name);
}
```

Essa interface define o contrato da busca. O domínio não depende de como ou onde a busca acontece — só que ela **deve** acontecer.

---
&nbsp;
## 4. Implementação - `YgoProApiClient`

```java
JsonNode response = feignClient.getCardsByName(name);
if (response.has("data")) {
    for (JsonNode node : response.get("data")) {
        CardFactory.fromJson(node).ifPresent(cards::add);
    }
}
```

O `YgoProApiClient` é um adapter de saída que implementa `CardSearchPort`. Ele usa o `YgoProFeignClient` para consultar a API e o `CardFactory` para converter JSON em objetos `Card`.

---

## 5. Implementação - `CardFactory`
Aqui usamos o design pattern factory que vai criar uma instância de um objeto de acordo com o tipo especifico de dado que for coletado, por esse motivo temos os condicionais presentes na classe. E por mais verboso que possa parecer todo esse tratamento ele fica totalmente encapsulado dentro do método fromJson.

```java
public class CardFactory {

    public static Optional<Card> fromJson(JsonNode node) {
        String type = node.get("type").asText();
        Long id = node.get("id").asLong();
        String name = node.get("name").asText();
        String desc = node.get("desc").asText();
        String archetype = node.has("archetype") ? node.get("archetype").asText() : null;
        String image = node.get("card_images").get(0).get("image_url").asText();

        if (type.contains("Monster")) {
            int atk = node.path("atk").asInt();
            int def = node.path("def").asInt();
            int level = node.path("level").asInt();
            MonsterAttribute attr = MonsterAttribute.valueOf(node.get("attribute").asText().toUpperCase());
            MonsterType monsterType = MonsterType.valueOf(normalize(node.get("race").asText()));
            Set<MonsterSubType> subTypes = detectMonsterSubtypes(type);

            return MonsterCard.create(id, name, desc, archetype, image, atk, def, level, attr, monsterType, subTypes)
                    .map(c -> (Card) c);
        }

        if (type.contains("Spell")) {
            SpellType spellType = SpellType.valueOf(normalize(node.get("race").asText()));
            return SpellCard.create(id, name, desc, archetype, image, spellType).map(c -> (Card) c);
        }

        if (type.contains("Trap")) {
            TrapType trapType = TrapType.valueOf(normalize(node.get("race").asText()));
            return TrapCard.create(id, name, desc, archetype, image, trapType).map(c -> (Card) c);
        }

        return Optional.empty();
    }

    private static String normalize(String input) {
        return input.trim().toUpperCase().replace("-", "_").replace(" ", "_");
    }

    private static Set<MonsterSubType> detectMonsterSubtypes(String rawType) {
        Set<MonsterSubType> set = EnumSet.noneOf(MonsterSubType.class);
        String[] parts = rawType.toUpperCase().split(" ");
        for (String part : parts) {
            try {
                set.add(MonsterSubType.valueOf(part));
            } catch (IllegalArgumentException ignored) {}
        }
        return set;
    }
}
```

&nbsp;
## 6. Conclusão

Este exemplo demonstra os princípios da arquitetura hexagonal:

- O **domínio** define a interface (CardSearchPort)
- O **controller** é um **adaptador de entrada**
- O **Feign client + factory** são **adaptadores de saída**

Esse modelo permite evoluir ou trocar qualquer parte da aplicação sem afetar o restante, promovendo testabilidade e manutenibilidade.

Repositório do github para consulta: https://github.com/odevpedro/yu-gi-oh-deck-management



