# Backlog — Yu-Gi-Oh! Deck Management

> Registro vivo do progresso do projeto. Atualizado a cada mudanca de estado de uma funcionalidade.
> **Ultima atualizacao:** 2026-07-07

---

## Sobre o Projeto

Sistema de gerenciamento de colecoes e decks de cartas Yu-Gi-Oh!, construido com arquitetura hexagonal em microservicos independentes que se comunicam via OpenFeign e Kafka.

**Versao atual:** `0.3.0`
**Repositorio:** [github.com/odevpedro/yu-gi-oh-deck-management](https://github.com/odevpedro/yu-gi-oh-deck-management)
**Stack principal:** Java 17 + Spring Boot 3.2 + Gradle + PostgreSQL + Kafka

---

## Legenda

| Simbolo | Significado |
|---------|-------------|
| `[ ]`   | Pendente |
| `[~]`   | Em andamento |
| `[x]`   | Concluido |
| `P0`    | Critico — bloqueia outras features |
| `P1`    | Alta prioridade |
| `P2`    | Media prioridade |
| `P3`    | Melhoria / nice-to-have |

---

## Em Andamento

> Nenhuma feature em andamento.

---

## Pendentes

### FASE 0 — Caminho Critico para o Duelo (impede jogar)

---

#### `[ ]` INT-001 — Consumer Kafka `duel.encerrado` no community-service

**Descricao:** O community-service precisa consumir o topico `duel.encerrado` publicado pelo duel-service para atualizar o `duelStatus` dos jogadores de `IN_DUEL` para `AVAILABLE`. Atualmente o topico e mencionado na documentacao (`docs/system-feature-flows.md`) mas o consumer nunca foi implementado.

**Onde:** `community-service`

**Checklist:**
- [ ] Adicionar dependencia `spring-kafka` (ja existe no build.gradle)
- [ ] Configurar Kafka consumer no `application.yml` do community-service:
  ```yaml
  spring:
    kafka:
      consumer:
        group-id: community-duel-group
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
        properties:
          spring.json.trusted.packages: "*"
        auto-offset-reset: earliest
  ```
- [ ] Criar DTO do evento recebido:
  ```java
  public record DuelEncerradoEvent(
      String duelId,
      String winnerId,
      String loserId,
      String playerAId,
      String playerBId,
      Integer turnCount,
      LocalDateTime finishedAt
  ) {}
  ```
- [ ] Criar classe `DuelEncerradoConsumer`:
  ```java
  @Component
  public class DuelEncerradoConsumer {
      private final PlayerRepositoryPort playerRepository;
      private final PlayerService playerService;

      @KafkaListener(topics = "duel.encerrado", groupId = "community-duel-group")
      public void consume(DuelEncerradoEvent event) {
          playerService.updateStatus(UUID.fromString(event.playerAId()), DuelStatus.AVAILABLE);
          playerService.updateStatus(UUID.fromString(event.playerBId()), DuelStatus.AVAILABLE);
      }
  }
  ```
- [ ] Tratar erros: se Kafka estiver indisponivel, logar warning e tentar novamente
- [ ] Testar com Testcontainers Kafka (ou manualmente publicando no topico)

**Criterio de aceitacao:** Quando um evento `duel.encerrado` e publicado no Kafka, ambos os jogadores tem `duelStatus` alterado para `AVAILABLE`.

**Depende de:** GAME-004 no duel-service

**Estimativa:** M

---

#### `[ ]` INT-002 — ChallengeService.accept() deve criar duelo no duel-service

**Descricao:** Quando um jogador aceita um desafio (`ChallengeService.accept()`), o sistema so atualiza o status do desafio para `ACCEPTED` e marca os jogadores como `IN_DUEL`. Falta chamar o duel-service para criar o duelo de fato.

**Fluxo desejado:**
1. Jogador A aceita desafio de Jogador B
2. `ChallengeServiceImpl.accept()` atualiza status e duelStatus
3. Community-service chama `POST /api/duels` no duel-service via Feign
4. Duelo e criado e retorna `duelId`
5. Community-service notifica os jogadores (via Kafka ou WebSocket) com o `duelId` para que conectem

**Onde:** `ChallengeServiceImpl.accept()`

**Checklist:**
- [ ] Criar `DuelFeignClient` no community-service:
  ```java
  @FeignClient(name = "duel-service", url = "${duel-service.url:http://localhost:8084}")
  public interface DuelFeignClient {
      @PostMapping("/api/duels")
      DuelResponseDTO createDuel(@RequestBody CreateDuelRequestDTO request);
  }
  ```
- [ ] Criar DTOs:
  ```java
  public record CreateDuelRequestDTO(String playerAId, String playerBId,
                                      Long playerADeckId, Long playerBDeckId) {}
  public record DuelResponseDTO(String duelId, String playerAId, String playerBId,
                                 String currentPhase, String status,
                                 int turnNumber, String activePlayerId) {}
  ```
- [ ] No `ChallengeServiceImpl.accept()`:
  ```java
  // Apos atualizar status do desafio
  DuelResponseDTO duel = duelFeignClient.createDuel(
      new CreateDuelRequestDTO(
          challenge.challengerId().toString(),
          challenge.targetId().toString(),
          challenge.challengerDeckId(),
          targetDeckId // precisa vir do accept request
      )
  );
  ```
- [ ] Ajustar `RespondChallengeRequest` para incluir `targetDeckId` (Long)
- [ ] Publicar evento `duel.iniciado` no Kafka ou mensagem WebSocket com `duelId` para os jogadores
- [ ] Configurar `duel-service.url` no `application.yml` do community-service:
  ```yaml
  duel-service:
    url: ${DUEL_SERVICE_URL:http://localhost:8084}
  ```

**Criterio de aceitacao:** Ao aceitar um desafio, um duelo e criado no duel-service e o `duelId` e retornado na resposta.

**Depende de:** BUG-002, BUG-003, BUG-004 do duel-service (para o duel-service estar funcional)

**Estimativa:** M

---

#### `[ ]` INT-003 — Adicionar targetDeckId no fluxo de aceite de desafio

**Descricao:** Atualmente `ChallengeController` recebe `RespondChallengeRequest` com apenas `ChallengeAction (ACCEPT/DECLINE)`. Para criar o duelo, precisamos tambem do `deckId` que o jogador alvo (quem aceita) usara.

**Checklist:**
- [ ] Adicionar campo `targetDeckId` (Long) no `RespondChallengeRequest`
- [ ] Validar que `targetDeckId` nao e null quando action = ACCEPT
- [ ] Passar `targetDeckId` para o `ChallengeService.accept()`
- [ ] Atualizar `ChallengeServiceImpl.accept()` para aceitar `targetDeckId`
- [ ] Passar `targetDeckId` para o `DuelFeignClient.createDuel()`

**DTO resultante:**
```java
public record RespondChallengeRequest(
    @NotNull ChallengeAction action,
    Long targetDeckId  // obrigatorio quando action = ACCEPT
) {}
```

**Criterio de aceitacao:** Aceitar desafio sem `targetDeckId` retorna erro de validacao 400.

**Estimativa:** S

---

### Autenticacao e Seguranca

---

#### `[ ]` AUTH-001 — Autenticacao centralizada com JWT e refresh token

**Status:** Implementado em auth-service (ver Concluidas).

---

#### `[ ]` AUTH-002 — Logout e revogacao de tokens

**Status:** Implementado (ver Concluidas).

---

#### `[ ]` AUTH-003 — Filtro JWT compartilhado via shared-domain com blacklist

**Status:** Implementado (ver Concluidas).

---

### Core Features

---

#### `[ ]` CORE-001 — Validacao de regras de deck (40-60 main, max 15 extra/side, max 3 copias)

**Status:** Implementado em deck-service (ver Concluidas).

---

#### `[ ]` CORE-002 — Import de deck via arquivo .ydk

**Descricao:** Usuario deve poder importar um deck a partir de um arquivo .ydk (formato padrao do YGOPro/DuelingBook).

**Checklist:**
- [ ] Criar endpoint `POST /decks/import` que aceita multipart file
- [ ] Parsing do .ydk:
  - Linhas com `#main` delimitam main deck
  - Linhas com `#extra` delimitam extra deck
  - Linhas com `!side` delimitam side deck
  - Cada linha e um cardId numerico
- [ ] Validar cartas contra card-service (verificar se existem)
- [ ] Aplicar `DeckValidator.validateDeck()` apos import
- [ ] Retornar `DeckView` com resultados da validacao

**Estimativa:** M

---

#### `[ ]` CORE-003 — Sincronizacao de deck via Kafka entre servicos

**Descricao:** Quando um deck e modificado no deck-service, publicar evento no Kafka para que outros servicos (ex: community-service, duel-service) possam reagir.

**Estimativa:** L

---

### FASE 0B — Configuracao Critica (impede comunicacao entre servicos)

---

#### `[ ]` CONFIG-001 — CORS em todos os servicos

**Descricao:** Nenhum servico tem CORS configurado. O frontend em `localhost:5173` nao consegue chamar nenhum endpoint.

**Onde:** Todos os servicos (auth, card, deck, community, card-creator, proxy, duel)

**Checklist:**
- [ ] Adicionar `@Bean WebMvcConfigurer` com CORS em cada servico
- [ ] Origem permitida: `http://localhost:5173`, `http://localhost:3000`
- [ ] `allowCredentials = true` para envio de cookies/Authorization header
- [ ] Se criar Gateway (INFRA-002), configurar CORS apenas nele

**Estimativa:** M

---

#### `[ ]` CONFIG-002 — Sincronizar portas entre servicos e documentacao

**Descricao:** As portas estao inconsistentes entre servicos e docs, impedindo a comunicacao correta.

| Servico | Porta real | Documentado como |
|---------|-----------|------------------|
| deck-service | 8081 | 8082 (no duel-service) |
| proxy-service | 8085 | 8082 (no README) |

**Checklist:**
- [ ] Corrigir `deck-service.url` para `8081` no `duel-service/application.yml`
- [ ] Corrigir README do monorepo: proxy-service = 8085
- [ ] Garantir que todos os servicos usem as mesmas portas de referencia

**Estimativa:** S

---

### Infraestrutura

---

#### `[ ]` INFRA-001 — Elasticsearch

**Descricao:** Busca full-text de cartas por descricao de efeito, indexando dados do YGOPRODeck.

**Estimativa:** L

---

#### `[ ]` INFRA-002 — API Gateway com Spring Cloud Gateway

**Descricao:** Criar um API Gateway que roteie requisicoes do frontend para o servico correto com base no path, eliminando a necessidade do frontend conhecer a porta de cada servico.

**Por que e importante para o jogo:** O frontend hoje precisaria saber 6 portas diferentes (8080-8087). Um Gateway unifica tudo em uma unica URL.

**Checklist:**
- [ ] Criar modulo `gateway-service` no monorepo
- [ ] Configurar Spring Cloud Gateway com rotas:
  ```yaml
  spring:
    cloud:
      gateway:
        routes:
          - id: auth-service
            uri: http://localhost:8086
            predicates:
              - Path=/auth/**
          - id: deck-service
            uri: http://localhost:8081
            predicates:
              - Path=/decks/**
          - id: card-service
            uri: http://localhost:8080
            predicates:
              - Path=/cards/**
          - id: community-service
            uri: http://localhost:8087
            predicates:
              - Path=/players/**, /challenges/**
          - id: duel-service
            uri: http://localhost:8084
            predicates:
              - Path=/api/duels/**
          - id: card-creator-service
            uri: http://localhost:8083
            predicates:
              - Path=/custom-cards/**
          - id: proxy-service
            uri: http://localhost:8085
            predicates:
              - Path=/proxy/**
  ```
- [ ] Adicionar CORS configurado para a URL do frontend
- [ ] Configurar rate limiting basico

**Criterio de aceitacao:** Frontend faz todas as chamadas para `http://gateway:8080/` e e roteado para o servico correto.

**Estimativa:** M

---

#### `[ ]` INFRA-003 — Prometheus + Grafana

**Estimativa:** M

---

#### `[ ]` INFRA-004 — Zipkin

**Estimativa:** M

---

#### `[ ]` INFRA-005 — build.gradle raiz com gerenciamento centralizado de versoes

**Descricao:** O `build.gradle` raiz esta vazio. Cada submodulo declara suas proprias versoes do Spring Boot e dependencias, causando inconsistencia.

**Checklist:**
- [ ] Centralizar versoes no `build.gradle` raiz:
  ```groovy
  subprojects {
      apply plugin: 'java'
      group = 'com.odevpedro'
      version = '0.3.0'
      repositories { mavenCentral() }
  }
  ```
- [ ] Extrair versoes para `ext` ou `gradle.properties`

**Estimativa:** S

---

#### `[ ]` INFRA-006 — docker-compose com servicos da aplicacao

**Descricao:** Atualmente o `docker-compose.yml` so sobe infra (DBs + Kafka). Adicionar os servicos Spring Boot para facilitar deploy local completo.

**Checklist:**
- [ ] Adicionar servicos no docker-compose.yml:
  ```yaml
  auth-service:
    build: ./auth-service
    ports: ["8086:8086"]
    depends_on: [auth-db]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://auth-db:5432/authdb
  ```
- [ ] Repetir para cada servico
- [ ] Configurar network unificada

**Estimativa:** L

---

### Testes

---

#### `[ ]` TEST-001 — Testes unitarios com JUnit

**Checklist:**
- [ ] auth-service: `JwtServiceTest`, `AuthServiceTest`
- [ ] deck-service: `DeckValidatorTest`, `DeckExportServiceTest`
- [ ] community-service: `ChallengeServiceTest`, `PlayerServiceTest`
- [ ] card-service: `SearchCardsUseCaseTest`
- [ ] card-creator-service: `CustomCardServiceTest`

**Estimativa:** L

---

#### `[ ]` TEST-002 — Testes de integracao com Testcontainers

**Checklist:**
- [ ] PostgreSQL: repositorios JPA
- [ ] Kafka: fluxo de eventos (card.created, duel.encerrado)
- [ ] Feign: mocks de servicos externos

**Estimativa:** L

---

---

### Documentacao e Ferramentas

---

#### `[ ]` DOC-001 — Collection Postman global

**Descricao:** Collection Postman/Insomnia com todas as chamadas dos 7 servicos para facilitar testes manuais e onboarding de devs.

**Checklist:**
- [ ] Criar `docs/api/duel-service.postman_collection.json`
- [ ] Variaveis de ambiente: `{{auth-api}}`, `{{deck-api}}`, etc.
- [ ] Requests organizados por servico em pastas

**Estimativa:** M

---

#### `[ ]` DOC-002 — Architecture Decision Records (ADRs)

**Descricao:** Decisoes arquiteturais importantes nao estao registradas (ex: porque Kafka? porque Hexagonal? porque JNI?).

**Checklist:**
- [ ] Criar diretorio `docs/adr/`
- [ ] ADR-001: Uso de Kafka para eventos assincronos
- [ ] ADR-002: Arquitetura Hexagonal (Ports & Adapters)
- [ ] ADR-003: JNI com ocgcore vs engine Java pura
- [ ] ADR-004: Redis como state store vs banco relacional
- [ ] Usar formato Michael Nygard (contexto, decisao, consequencias, status)

**Estimativa:** M

---

### Seguranca

---

#### `[ ]` AUTH-004 — Rate limiting no login

**Descricao:** Sem limite de tentativas de login, ataque de forca bruta e trivial.

**Onde:** `auth-service`

**Checklist:**
- [ ] Implementar rate limiter por IP no endpoint `POST /auth/login`
- [ ] 5 tentativas falhas em 1 minuto → bloquear por 5 minutos
- [ ] Usar `bucket4j` ou implementacao simples com `ConcurrentHashMap<String, List<Instant>>`
- [ ] Retornar `429 Too Many Requests` com header `Retry-After`

**Estimativa:** M

---

#### `[ ]` AUTH-005 — Verificacao de email

**Descricao:** Usuarios podem se registrar com qualquer email, inclusive invalido. Sem verificacao, contas fantasmas proliferam.

**Checklist:**
- [ ] Ao registrar, enviar email com link de confirmacao
- [ ] Adicionar campo `email_verified` (boolean) no `UserEntity`
- [ ] Endpoint `GET /auth/verify?token=...` que marca como verificado
- [ ] Opcional: servico de email (SendGrid, Mailgun, ou SMTP)

**Estimativa:** L

---

#### `[ ]` AUTH-006 — Reset de senha

**Checklist:**
- [ ] `POST /auth/forgot-password` — recebe email, envia token de reset
- [ ] `POST /auth/reset-password` — recebe token + nova senha
- [ ] Token de reset expira em 15 minutos

**Estimativa:** M

---

#### `[ ]` SEC-001 — Correlation ID entre servicos

**Descricao:** Sem correlationId, e impossivel rastrear uma requisicao que passa por 3 servicos (ex: frontend → gateway → comunidade → duelo).

**Checklist:**
- [ ] Adicionar filtro `CorrelationIdFilter` no `shared-domain`
- [ ] Se header `X-Correlation-Id` existir, propaga-lo; se nao, gerar UUID
- [ ] Configurar `logging.pattern.level` em todos os servicos para incluir `[%X{correlationId}]`
- [ ] Propagar via Feign `RequestInterceptor`

**Estimativa:** M

---

#### `[ ]` SEC-002 — Circuit breaker no deck-service para card-service

**Descricao:** Se card-service estiver fora, as chamadas do deck-service ficam pendentes e consomem threads.

**Onde:** `deck-service` — ja tem resilience4j configurado mas `CardFeignClient` pode nao estar usando

**Checklist:**
- [ ] Verificar se `@CircuitBreaker(name = "cardCatalog")` esta aplicado nas chamadas ao CardFeignClient
- [ ] Testar fallback: se card-service offline, retornar dados parciais com aviso

**Estimativa:** S

---

### Testes

---

#### `[ ]` TEST-003 — Testes de integracao Kafka (fluxo completo desafio -> duelo -> encerramento)

---

#### `[ ]` TEST-004 — Testes end-to-end com Postman/Newman

**Checklist:**
- [ ] Fluxo: registrar usuario → login → criar deck → criar duelo → ver historico
- [ ] Script Newman para CI: `newman run collection.json -e environment.json`
- [ ] Incluir assertions de status code e body

**Estimativa:** L

**Descricao:** Testar o fluxo completo: criar desafio -> aceitar -> duel-service criar duelo -> duel-service publicar duel.encerrado -> community-service consumir e atualizar status.

**Checklist:**
- [ ] Usar Testcontainers com Kafka + PostgreSQL
- [ ] Mockar DuelFeignClient
- [ ] Publicar evento `duel.encerrado` manualmente
- [ ] Verificar que `Player.duelStatus` foi alterado para AVAILABLE

**Estimativa:** M

---

## Concluidas

### Arquitetura

| ID | Feature | Data | Referencia |
|----|---------|------|------------|
| ARCH-001 | Arquitetura Hexagonal (Ports & Adapters) | 2024 | hex-arch |
| ARCH-002 | Centralizacao de rotas em ApiRoutes | 2026-04-28 | shared-domain |
| ARCH-003 | Filtro JWT compartilhado via shared-domain com blacklist | 2026-04-28 | JwtAuthFilter |

### Features de Autenticacao

| ID | Feature | Data | Referencia |
|----|---------|------|------------|
| AUTH-001 | JWT com access token e refresh token | 2026-04-28 | JwtService, RefreshTokenService |
| AUTH-002 | Logout com blacklist de tokens | 2026-04-28 | TokenBlacklistService |
| AUTH-003 | Validacao de token via Feign (blacklist check) | 2026-04-28 | TokenValidationClient, JwtAuthFilter |

### Servicos Implementados

| ID | Servico | Data | Referencia |
|----|---------|------|------------|
| SVC-001 | card-service — consulta ao catalogo via YGOPRODeck API com cache | 2024 | card-service |
| SVC-002 | deck-service — criacao e composicao de decks com export .ydk | 2024 | deck-service |
| SVC-003 | proxy-service — geracao de PDFs de cartas para impressao | 2024 | proxy-service |
| SVC-004 | card-creator-service — criacao de cartas customizadas com validacao assincrona | 2024 | card-creator-service |
| SVC-005 | community-service — geolocalizacao de jogadores e sistema de desafio de duelo | 2024 | community-service |
| SVC-006 | auth-service — autenticacao e emissao de JWT | 2024 | auth-service |
| SVC-007 | shared-domain — biblioteca interna com enums e filtro JWT compartilhados | 2024 | shared-domain |

### Features de Deck

| ID | Feature | Data | Referencia |
|----|---------|------|------------|
| DECK-001 | CRUD completo de decks | 2024 | DeckController |
| DECK-002 | Adicionar/remover cartas do deck | 2024 | DeckController |
| DECK-003 | Export de deck no formato .ydk | 2024 | DeckExportService |
| DECK-004 | Busca de cartas via YGOPRODeck API | 2024 | CardController |
| DECK-005 | Validacao de regras de deck Yu-Gi-Oh! | 2026-04-28 | DeckValidator |
| DECK-006 | Retorno de validacao no endpoint /decks/{id}/full | 2026-04-28 | DeckView |

### Features de Cards Customizados

| ID | Feature | Data | Referencia |
|----|---------|------|------------|
| CARD-001 | Criacao de cartas customizadas | 2024 | CustomCardController |
| CARD-002 | Validacao de stats (ATK/DEF 0-5000, Level 1-12) | 2024 | CustomCard domain |
| CARD-003 | Validacao de texto (nome max 255, efeito max 2000) | 2024 | CustomCard domain |
| CARD-004 | Publicacao de eventos via Kafka (card.created) | 2024 | Kafka producer |

### Features de Comunidade

| ID | Feature | Data | Referencia |
|----|---------|------|------------|
| COMM-001 | Registro de jogadores com geolocalizacao | 2024 | PlayerController |
| COMM-002 | Busca de jogadores proximos via PostGIS | 2024 | PlayerService |
| COMM-003 | Sistema de desafios de duelo | 2024 | ChallengeController |
| COMM-004 | Expiracao automatica de desafios | 2024 | @Scheduled |

---

## Bugs Conhecidos

| ID | Descricao | Severidade | Reportado em |
|----|-----------|------------|--------------|
| BUG-007 | Proxy-service roda na porta 8085 mas documentacao do README diz 8082 | Media | 2026-07-07 |
| BUG-008 | GlobalExceptionHandler vazio em auth-service e card-creator-service (arquivos existem mas sem conteudo) | Media | 2026-07-07 |
| BUG-009 | CardResponseDTO em deck-service parece dead code (nao usado por nenhum servico) | Baixa | 2026-07-07 |
| BUG-010 | duel-service aponta deck-service.url para 8082, mas deck-service real roda na 8081 | Media | 2026-07-07 |
| BUG-011 | Proxy-service roda na 8085 mas README documenta como 8082 — documentacao inconsistente | Media | 2026-07-07 |

---

## Notas e Decisoes Pendentes

- [ ] Decidir estrategia de sincronizacao entre deck-service e card-service
- [x] Definir formato de eventos Kafka entre servicos
- [ ] Configurar CORS para permitir acesso de origens externas
- [ ] Implementar rate limiting no API Gateway
- [ ] Decidir se duel-service sera incorporado como submodulo Gradle ou mantido como repo separado
- [ ] Definir versao unificada do Spring Cloud BOM para todos os modulos
- [ ] Sincronizar portas entre servicos: duel-service aponta deck-service na 8082, mas deck-service real na 8081; proxy-service na 8085 mas docs dizem 8082

---

## Historico de Versoes

| Versao | Data | Principais entregas |
|--------|------|---------------------|
| `0.3.0` | 2026-04-28 | JWT com refresh token, logout com blacklist, validacao via Feign |
| `0.2.0` | 2026-04-28 | Centralizacao de rotas em ApiRoutes, community-service e auth-service implementados |
| `0.1.0` | 2024 | MVP com card-service, deck-service, proxy-service, card-creator-service |
