# Backlog — Yu-Gi-Oh! Deck Management

> Registro vivo do progresso do projeto. Atualizado a cada mudanca de estado de uma funcionalidade.
> **Ultima atualizacao:** 2026-07-08 — Sessao de trabalho: auth-service (AUTH-005/006), deck-service (CORE-002/003), gateway-service, docker-compose, testes unitarios e integracao

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

#### `[x]` INT-001 — Consumer Kafka `duel.encerrado` no community-service

**Descricao:** O community-service precisa consumir o topico `duel.encerrado` publicado pelo duel-service para atualizar o `duelStatus` dos jogadores de `IN_DUEL` para `AVAILABLE`. Atualmente o topico e mencionado na documentacao (`docs/system-feature-flows.md`) mas o consumer nunca foi implementado.

**Onde:** `community-service`

**Checklist:**
- [x] Adicionar dependencia `spring-kafka` (ja existe no build.gradle)
- [x] Configurar Kafka consumer no `application.yml` do community-service:
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
- [x] Consumir o evento sem acoplar DTO compartilhado entre repositorios
- [x] Criar classe `DuelLifecycleKafkaListener`
- [x] Tratar erros: se Kafka estiver indisponivel, logar warning e nao travar o fluxo
- [x] Testar com Testcontainers Kafka (ou manualmente publicando no topico)

**Criterio de aceitacao:** Quando um evento `duel.encerrado` e publicado no Kafka, ambos os jogadores tem `duelStatus` alterado para `AVAILABLE`.

**Depende de:** GAME-004 no duel-service

**Estimativa:** M

---

#### `[x]` INT-002 — ChallengeService.accept() deve criar duelo no duel-service

**Descricao:** Quando um jogador aceita um desafio (`ChallengeService.accept()`), o sistema so atualiza o status do desafio para `ACCEPTED` e marca os jogadores como `IN_DUEL`. Falta chamar o duel-service para criar o duelo de fato.

**Fluxo desejado:**
1. Jogador A aceita desafio de Jogador B
2. `ChallengeServiceImpl.accept()` atualiza status e duelStatus
3. Community-service chama `POST /api/duels` no duel-service via Feign
4. Duelo e criado e retorna `duelId`
5. Community-service notifica os jogadores (via Kafka ou WebSocket) com o `duelId` para que conectem

**Onde:** `ChallengeServiceImpl.accept()`

**Checklist:**
- [x] Criar `DuelFeignClient` no community-service:
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
- [x] No `ChallengeServiceImpl.accept()`:
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
- [x] Ajustar `RespondChallengeRequest` para incluir `targetDeckId` (Long)
- [x] Publicar evento `duel.iniciado` no Kafka ou mensagem WebSocket com `duelId` para os jogadores
- [x] Configurar `duel-service.url` no `application.yml` do community-service:
  ```yaml
  duel-service:
    url: ${DUEL_SERVICE_URL:http://localhost:8084}
  ```

**Criterio de aceitacao:** Ao aceitar um desafio, um duelo e criado no duel-service e o `duelId` e retornado na resposta.

**Depende de:** — (bugs BUG-002, BUG-003, BUG-004 do duel-service foram corrigidos em 2026-07-07)

**Estimativa:** M

---

#### `[x]` INT-003 — Adicionar targetDeckId no fluxo de aceite de desafio

**Descricao:** Atualmente `ChallengeController` recebe `RespondChallengeRequest` com apenas `ChallengeAction (ACCEPT/DECLINE)`. Para criar o duelo, precisamos tambem do `deckId` que o jogador alvo (quem aceita) usara.

**Checklist:**
- [x] Adicionar campo `targetDeckId` (Long) no `RespondChallengeRequest`
- [x] Validar que `targetDeckId` nao e null quando action = ACCEPT
- [x] Passar `targetDeckId` para o `ChallengeService.accept()`
- [x] Atualizar `ChallengeServiceImpl.accept()` para aceitar `targetDeckId`
- [x] Passar `targetDeckId` para o `DuelFeignClient.createDuel()`

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

#### `[x]` AUTH-001 — Autenticacao centralizada com JWT e refresh token

**Status:** Implementado em auth-service (ver Concluidas).

---

#### `[x]` AUTH-002 — Logout e revogacao de tokens

**Status:** Implementado (ver Concluidas).

---

#### `[x]` AUTH-003 — Filtro JWT compartilhado via shared-domain com blacklist

**Status:** Implementado (ver Concluidas).

---

### Core Features

---

#### `[x]` CORE-001 — Validacao de regras de deck (40-60 main, max 15 extra/side, max 3 copias)

**Status:** Implementado em deck-service (ver Concluidas).

---

#### `[x]` CORE-002 — Import de deck via arquivo .ydk

**Descricao:** Usuario deve poder importar um deck a partir de um arquivo .ydk (formato padrao do YGOPro/DuelingBook).

**Checklist:**
- [x] Criar endpoint `POST /decks/import` que aceita multipart file
- [x] Parsing do .ydk:
  - Linhas com `#main` delimitam main deck
  - Linhas com `#extra` delimitam extra deck
  - Linhas com `!side` delimitam side deck
  - Cada linha e um cardId numerico
- [x] Validar cartas contra card-service (verificar se existem)
- [x] Aplicar `DeckValidator.validateDeck()` apos import
- [x] Retornar `DeckView` com resultados da validacao

**Estimativa:** M

---

#### `[x]` CORE-003 — Sincronizacao de deck via Kafka entre servicos

**Descricao:** Quando um deck e modificado no deck-service, publicar evento no Kafka para que outros servicos (ex: community-service, duel-service) possam reagir. O deck-service ja emite o evento `deck.synced` nas mutacoes principais.

**Estimativa:** L

---

### FASE 0B — Configuracao Critica (impede comunicacao entre servicos)

---

#### `[x]` CONFIG-001 — CORS em todos os servicos

**Descricao:** Nenhum servico tem CORS configurado. O frontend em `localhost:5173` nao consegue chamar nenhum endpoint.

**Onde:** Todos os servicos (auth, card, deck, community, card-creator, proxy, duel)

**Checklist:**
- [x] Adicionar `@Bean WebMvcConfigurer` com CORS em cada servico
- [x] Origem permitida: `http://localhost:5173`, `http://localhost:3000`
- [x] `allowCredentials = true` para envio de cookies/Authorization header
- [x] Se criar Gateway (INFRA-002), configurar CORS apenas nele

**Estimativa:** M

---

#### `[x]` CONFIG-002 — Sincronizar portas entre servicos e documentacao

**Descricao:** As portas estao inconsistentes entre servicos e docs, impedindo a comunicacao correta.

| Servico | Porta real | Documentado como |
|---------|-----------|------------------|
| deck-service | 8081 | 8081 |
| proxy-service | 8085 | 8085 |

**Checklist:**
- [x] Corrigir `deck-service.url` para `8081` no `duel-service/application.yml` (feito em 2026-07-07)
- [x] Corrigir README do monorepo: proxy-service = 8085
- [x] Garantir que todos os servicos usem as mesmas portas de referencia

**Estimativa:** S

---

### Infraestrutura

---

#### `[ ]` INFRA-001 — Elasticsearch

**Descricao:** Busca full-text de cartas por descricao de efeito, indexando dados do YGOPRODeck.

**Estimativa:** L

---

#### `[x]` INFRA-002 — API Gateway com Spring Cloud Gateway

**Descricao:** Criar um API Gateway que roteie requisicoes do frontend para o servico correto com base no path, eliminando a necessidade do frontend conhecer a porta de cada servico.

**Por que e importante para o jogo:** O frontend hoje precisaria saber 6 portas diferentes (8080-8087). Um Gateway unifica tudo em uma unica URL.

**Checklist:**
- [x] Criar modulo `gateway-service` no monorepo
- [x] Configurar Spring Cloud Gateway com rotas:
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
- [x] Adicionar CORS configurado para a URL do frontend
- [x] Configurar rate limiting basico

**Criterio de aceitacao:** Frontend faz todas as chamadas para o gateway e e roteado para o servico correto. No monorepo atual, o gateway opera em `http://localhost:8088/` para nao colidir com o card-service.

**Estimativa:** M

---

#### `[ ]` INFRA-003 — Prometheus + Grafana

**Estimativa:** M

---

#### `[ ]` INFRA-004 — Zipkin

**Estimativa:** M

---

#### `[~]` INFRA-005 — build.gradle raiz com gerenciamento centralizado de versoes

**Descricao:** O `build.gradle` raiz agora concentra metadados e constantes comuns. Falta terminar de mover as versoes restantes para o topo e reduzir os literais espalhados pelos submodulos.

**Checklist:**
- [x] Centralizar versoes no `build.gradle` raiz:
  ```groovy
  subprojects {
      apply plugin: 'java'
      group = 'com.odevpedro'
      version = '0.3.0'
      repositories { mavenCentral() }
  }
  ```
- [~] Extrair versoes para `ext` ou `gradle.properties`

**Estimativa:** S

---

#### `[x]` INFRA-006 — docker-compose com servicos da aplicacao

**Descricao:** Atualmente o `docker-compose.yml` so sobe infra (DBs + Kafka). Adicionar os servicos Spring Boot para facilitar deploy local completo.

**Checklist:**
- [x] Adicionar servicos no docker-compose.yml:
  ```yaml
  auth-service:
    build: ./auth-service
    ports: ["8086:8086"]
    depends_on: [auth-db]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://auth-db:5432/authdb
  ```
- [x] Repetir para cada servico
- [x] Configurar network unificada

**Estimativa:** L

---

### Testes

---

#### `[x]` TEST-001 — Testes unitarios com JUnit

**Checklist:**
- [x] auth-service: `JwtServiceTest`, `AuthServiceTest`
- [x] deck-service: `DeckValidatorTest`, `DeckExportServiceTest`
- [x] community-service: `ChallengeServiceTest`, `PlayerServiceTest`
- [x] card-service: `SearchCardsUseCaseTest`
- [x] card-creator-service: `CustomCardServiceTest`

**Estimativa:** L

---

#### `[x]` TEST-002 — Testes de integracao com Testcontainers

**Checklist:**
- [x] PostgreSQL: repositorios JPA
- [x] Kafka: fluxo de eventos (card.created, duel.encerrado)
- [x] Feign: mocks de servicos externos

**Estimativa:** L

---

---

### Documentacao e Ferramentas

---

#### `[x]` DOC-001 — Collection Postman global

**Descricao:** Collection Postman/Insomnia com todas as chamadas dos 7 servicos para facilitar testes manuais e onboarding de devs.

**Checklist:**
- [x] Criar `docs/api/duel-service.postman_collection.json`
- [x] Variaveis de ambiente: `{{auth-api}}`, `{{deck-api}}`, etc.
- [x] Requests organizados por servico em pastas

**Estimativa:** M

---

#### `[x]` DOC-002 — Architecture Decision Records (ADRs)

**Descricao:** Decisoes arquiteturais importantes nao estao registradas (ex: porque Kafka? porque Hexagonal? porque JNI?).

**Checklist:**
- [x] Criar diretorio `docs/adr/`
- [x] ADR-001: Uso de Kafka para eventos assincronos
- [x] ADR-002: Arquitetura Hexagonal (Ports & Adapters)
- [x] ADR-003: JNI com ocgcore vs engine Java pura
- [x] ADR-004: Redis como state store vs banco relacional
- [x] Usar formato Michael Nygard (contexto, decisao, consequencias, status)

**Estimativa:** M

---

### Seguranca

---

#### `[x]` AUTH-004 — Rate limiting no login

**Descricao:** Sem limite de tentativas de login, ataque de forca bruta e trivial.

**Onde:** `auth-service`

**Checklist:**
- [x] Implementar rate limiter por IP no endpoint `POST /auth/login`
- [x] 5 tentativas falhas em 1 minuto → bloquear por 5 minutos
- [x] Usar `bucket4j` ou implementacao simples com `ConcurrentHashMap<String, List<Instant>>`
- [x] Retornar `429 Too Many Requests` com header `Retry-After`

**Estimativa:** M

---

#### `[x]` AUTH-005 — Verificacao de email

**Descricao:** Usuarios podem se registrar com qualquer email, inclusive invalido. Sem verificacao, contas fantasmas proliferam.

**Checklist:**
- [x] Ao registrar, enviar email com link de confirmacao
- [x] Adicionar campo `email_verified` (boolean) no `UserEntity`
- [x] Endpoint `GET /auth/verify?token=...` que marca como verificado
- [ ] Opcional: servico de email (SendGrid, Mailgun, ou SMTP)

**Estimativa:** L

---

#### `[x]` AUTH-006 — Reset de senha

**Checklist:**
- [x] `POST /auth/forgot-password` — recebe email, envia token de reset
- [x] `POST /auth/reset-password` — recebe token + nova senha
- [x] Token de reset expira em 15 minutos

**Estimativa:** M

---

#### `[x]` SEC-001 — Correlation ID entre servicos

**Descricao:** Sem correlationId, e impossivel rastrear uma requisicao que passa por 3 servicos (ex: frontend → gateway → comunidade → duelo).

**Checklist:**
- [x] Adicionar filtro `CorrelationIdFilter` no `shared-domain`
- [x] Se header `X-Correlation-Id` existir, propaga-lo; se nao, gerar UUID
- [x] Configurar `logging.pattern.level` em todos os servicos para incluir `[%X{correlationId}]`
- [x] Propagar via Feign `RequestInterceptor`

**Estimativa:** M

---

#### `[x]` SEC-002 — Circuit breaker no deck-service para card-service

**Descricao:** Se card-service estiver fora, as chamadas do deck-service ficam pendentes e consomem threads.

**Onde:** `deck-service` — ja tem resilience4j configurado mas `CardFeignClient` pode nao estar usando

**Checklist:**
- [x] Verificar se `@CircuitBreaker(name = "cardCatalog")` esta aplicado nas chamadas ao CardFeignClient
- [x] Testar fallback: se card-service offline, retornar dados parciais com aviso

**Estimativa:** S

---

### Testes

---

#### `[x]` TEST-003 — Testes de integracao Kafka (fluxo completo desafio -> duelo -> encerramento)

---

#### `[x]` TEST-004 — Testes end-to-end com Postman/Newman

**Checklist:**
- [x] Fluxo: registrar usuario → login → criar deck → criar duelo → ver historico
- [x] Script Newman para CI: `newman run collection.json -e environment.json`
- [x] Incluir assertions de status code e body

**Estimativa:** L

**Descricao:** Testar o fluxo completo: criar desafio -> aceitar -> duel-service criar duelo -> duel-service publicar duel.encerrado -> community-service consumir e atualizar status.

**Checklist:**
- [x] Usar Testcontainers com Kafka + PostgreSQL
- [x] Mockar DuelFeignClient
- [x] Publicar evento `duel.encerrado` manualmente
- [x] Verificar que `Player.duelStatus` foi alterado para AVAILABLE

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
| ~~BUG-007~~ | ~~Proxy-service roda na porta 8085 mas documentacao do README dizia 8082~~ — Corrigido em 2026-07-08 | Resolvido | 2026-07-08 |
| ~~BUG-008~~ | ~~GlobalExceptionHandler vazio em auth-service e card-creator-service~~ — Corrigido em 2026-07-09 | Resolvido | 2026-07-07 |
| ~~BUG-009~~ | ~~CardResponseDTO em deck-service parece dead code~~ — Corrigido em 2026-07-09 | Resolvido | 2026-07-07 |
| ~~BUG-010~~ | ~~duel-service aponta deck-service.url para 8082, mas deck-service real roda na 8081~~ — Corrigido em 2026-07-07 | Resolvido | 2026-07-07 |
| ~~BUG-011~~ | ~~Proxy-service roda na 8085 mas README documentava como 8082~~ — Corrigido em 2026-07-08 | Resolvido | 2026-07-08 |

---

## Notas e Decisoes Pendentes

- [ ] Decidir estrategia de sincronizacao entre deck-service e card-service
- [x] Definir formato de eventos Kafka entre servicos
- [x] Configurar CORS para permitir acesso de origens externas
- [x] Implementar rate limiting no API Gateway
- [ ] Decidir se duel-service sera incorporado como submodulo Gradle ou mantido como repo separado
- [ ] Definir versao unificada do Spring Cloud BOM para todos os modulos
- [x] Sincronizar portas entre servicos: proxy-service na 8085 mas docs diziam 8082
- [x] Sincronizar deck-service.url no duel-service: 8082 → 8081 (Corrigido em 2026-07-07)

---

## Historico de Versoes

| Versao | Data | Principais entregas |
|--------|------|---------------------|
| `0.3.0` | 2026-04-28 | JWT com refresh token, logout com blacklist, validacao via Feign |
| `0.2.0` | 2026-04-28 | Centralizacao de rotas em ApiRoutes, community-service e auth-service implementados |
| `0.1.0` | 2024 | MVP com card-service, deck-service, proxy-service, card-creator-service |
