# System Feature Flows

> Registro historico e incremental dos fluxos internos de cada funcionalidade.
> Este documento cresce a cada nova feature implementada e **nunca tem secoes removidas**.

---

## Indice

<!-- Atualize este indice a cada nova feature adicionada -->

- [Visão Geral da Arquitetura](#visao-geral-da-arquitetura)
- [Convencoes deste Documento](#convencoes-deste-documento)
- [ApiRoutes — Centralizacao de Rotas](#apiroutes--centralizacao-de-rotas)
- [Feature: Autenticacao JWT com Refresh Token](#feature-autenticacao-jwt-com-refresh-token)
- [Feature: Logout e Revogacao de Tokens](#feature-logout-e-revogacao-de-tokens)
- [Feature: Validacao de Token via Blacklist](#feature-validacao-de-token-via-blacklist)
- [Feature: Validacao de Regras de Deck](#feature-validacao-de-regras-de-deck)
- [Feature: Gerenciamento de Decks](#feature-gerenciamento-de-decks)
- [Feature: Busca de Cartas](#feature-busca-de-cartas)
- [Feature: Criacao de Cartas Customizadas](#feature-criacao-de-cartas-customizadas)
- [Feature: Geracao de Proxy PDF](#feature-geracao-de-proxy-pdf)
- [Feature: Comunidade e Desafios](#feature-comunidade-e-desafios)

---

## Visao Geral da Arquitetura

> Descreva aqui a arquitetura geral do sistema — uma vez, no topo. As features abaixo assumem esse contexto.

**Padrao arquitetural:** Hexagonal (Ports & Adapters)

**Fluxo global de uma requisicao:**

```
HTTP Request
    └── Controller (adapter/in/rest)
            └── Use Case (application/service)
                    ├── Domain Entity / Domain Service
                    └── Repository / Gateway (adapter/out/)
                              └── Database / API Externa
```

**Microservicos e responsabilidades:**

| Servico | Porta | Responsabilidade |
|---------|-------|-----------------|
| card-service | 8080 | Consulta ao catalogo de cartas via YGOPRODeck API com cache |
| deck-service | 8081 | Criacao e composicao de decks com export/import .ydk |
| proxy-service | 8082 | Geracao de PDFs de cartas para impressao de proxies |
| card-creator-service | 8083 | Criacao de cartas customizadas com validacao assincrona |
| community-service | 8085 | Geolocalizacao de jogadores e sistema de desafio de duelo |
| auth-service | 8086 | Autenticacao e emissao de JWT |
| shared-domain | — | Biblioteca interna com enums e filtro JWT compartilhados |

**Camadas e responsabilidades:**

| Camada | Responsabilidade |
|--------|-----------------|
| `adapter/in/rest` | Receber requisicoes HTTP, validar DTOs, formatar resposta |
| `application/service` | Orquestrar o caso de uso, coordenar dominio e infra |
| `domain/model` | Regras de negocio puras, entidades, value objects |
| `adapter/out/` | Persistencia, Feign, Kafka, API externa |

---

## Convencoes deste Documento

- **Rotas centralizadas** via `ApiRoutes` em `shared-domain/src/main/java/com/odevpedro/yugiohcollections/shared/constants/ApiRoutes.java`
- **Erros de dominio** sao lancados como excecoes tipadas
- **Erros de infra** sao capturados e relancados como erros de aplicacao
- **Transacoes de banco** sao gerenciadas no nivel do service, nao do repository
- **DTOs** trafegam entre presentation ↔ application; **Entidades** entre application ↔ domain

---
---

# ApiRoutes — Centralizacao de Rotas

> **Versao:** 1.0.0
> **Implementada em:** 2026-04-28
> **Status:** Concluida

---

## Resumo

Todas as rotas da aplicacao foram centralizadas na classe `ApiRoutes` do modulo `shared-domain`, permitindo manutencao unificada e evitando inconsistencias entre controllers.

**Motivacao:** Rotas hardcoded nos controllers causavam duplicacao e risco de不一致.
**Resultado:** Todas as rotas agora referenciam constantes de `ApiRoutes`.

---

## Rotas Definidas

### Auth

```java
public static final String AUTH_BASE = "/auth";
public static final String AUTH_REGISTER = AUTH_BASE + "/register";
public static final String AUTH_LOGIN = AUTH_BASE + "/login";
public static final String AUTH_ME = AUTH_BASE + "/me";
```

### Decks

```java
public static final String DECKS_BASE = "/decks";
public static final String DECKS_BY_ID = DECKS_BASE + ID;
public static final String DECKS_FULL = "/decks/{deckId}/full";
public static final String DECKS_CARDS = "/decks/{deckId}/cards";
public static final String DECKS_EXPORT = "/decks/{deckId}/export";
```

### Cards

```java
public static final String CARDS_BASE = "/cards";
public static final String CARDS_INTERNAL = "/internal";
public static final String CARDS_INTERNAL_BY_ID = CARDS_INTERNAL + ID;
```

### Custom Cards

```java
public static final String CUSTOM_CARDS_BASE = "/custom-cards";
public static final String CUSTOM_CARDS_BY_ID = CUSTOM_CARDS_BASE + ID;
```

### Players

```java
public static final String PLAYERS_BASE = "/players";
public static final String PLAYERS_ME_STATUS = PLAYERS_BASE + "/me/status";
public static final String PLAYERS_NEARBY = PLAYERS_BASE + "/nearby";
```

### Challenges

```java
public static final String CHALLENGES_BASE = "/challenges";
public static final String CHALLENGES_BY_ID = CHALLENGES_BASE + ID;
public static final String CHALLENGES_PENDING = CHALLENGES_BASE + "/pending";
```

### Proxy

```java
public static final String PROXY_BASE = "/proxy";
public static final String PROXY_BY_ID = PROXY_BASE + ID;
```

---

## Arquivos Modificados

| Arquivo | Mudanca |
|---------|--------|
| `shared-domain/.../constants/ApiRoutes.java` | Adicionadas todas as constantes de rota |
| `auth-service/.../AuthController.java` | `@RequestMapping(ApiRoutes.AUTH_BASE)` |
| `deck-service/.../DeckController.java` | `@RequestMapping(ApiRoutes.DECKS_BASE)` |
| `player-service/.../PlayerController.java` | `@RequestMapping(ApiRoutes.PLAYERS_BASE)` |
| `challenge-service/.../ChallengeController.java` | `@RequestMapping(ApiRoutes.CHALLENGES_BASE)` |
| `proxy-service/.../ProxyController.java` | `@RequestMapping(ApiRoutes.PROXY_BASE)` |

---
---

# Feature: Autenticacao JWT com Refresh Token

> **Versao:** 2.0.0
> **Implementada em:** 2026-04-28
> **Status:** Concluida

---

## Resumo

Sistema de autenticacao com dois tipos de tokens: access token (curto prazo, 1 hora) e refresh token (longo prazo, 7 dias). O refresh token permite renovacao do access token sem necessidade de novo login.

**Motivacao:** Access tokens curtos aumentam a seguranca, mas causam fricao ao usuario. Refresh tokens resolvem esse problema.
**Resultado:** Login una vez, renovacao automatica de tokens por ate 7 dias.

---

## Fluxo Principal

### 1. Ponto de Entrada

- **Tipo:** HTTP REST
- **Arquivo:** `auth-service/src/main/java/com/odevpedro/yugiohcollections/auth/adapter/in/rest/AuthController.java`
- **Rotas:** `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`
- **Autenticacao:** Publica

### 2. Validação de Entrada

| Campo | Tipo | Obrigatorio | Regra de validacao |
|-------|------|-------------|---------------------|
| username | String | Sim | 3-50 caracteres |
| email | String | Sim | Formato de email valido |
| password | String | Sim | Minimo 8 caracteres |
| refreshToken | String | Sim | Token valido existente |

### 3. Regras de Negocio

| Regra | Descricao | Localizacao no Codigo |
|-------|-----------|----------------------|
| Access Token | Valido por 1 hora | `JwtService.generateToken()` |
| Refresh Token | Valido por 7 dias | `JwtService.generateRefreshToken()` |
| Um refresh token por vez | Refresh tokens anteriores sao revocados | `RefreshTokenService` |

### 4. Integracoes

| Servico | Operacao | Timeout | Retry |
|---------|----------|---------|-------|
| RefreshTokenRepository | Persistencia de tokens | - | - |

### 5. Resposta Final

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "a8f3b2c1d4e5f6...",
  "tokenType": "Bearer",
  "expiresIn": 3600000,
  "username": "player1",
  "email": "player1@yugioh.com",
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## Diagrama de Sequencia

```mermaid
sequenceDiagram
    actor Client
    participant AuthController
    participant AuthService
    participant JwtService
    participant RefreshTokenService
    participant UserRepository
    participant DB

    Client->>AuthController: POST /auth/login
    AuthController->>AuthService: login(request)
    AuthService->>UserRepository: findByUsername(username)
    UserRepository->>DB: SELECT user
    DB-->>UserRepository: user data
    UserRepository-->>AuthService: user entity
    AuthService->>JwtService: generateToken(userId, username, role)
    JwtService-->>AuthService: accessToken
    AuthService->>JwtService: generateRefreshToken(userId, username, role)
    JwtService-->>AuthService: refreshToken
    AuthService->>RefreshTokenService: createRefreshToken(user)
    RefreshTokenService->>DB: save refresh_token
    DB-->>RefreshTokenService: saved
    RefreshTokenService-->>AuthService: refresh token entity
    AuthService-->>AuthController: AuthResponse
    AuthController-->>Client: 200 OK + tokens

    Client->>AuthController: POST /auth/refresh
    AuthController->>AuthService: refreshToken(request)
    AuthService->>RefreshTokenService: validateRefreshToken(token)
    RefreshTokenService->>DB: find by token
    DB-->>RefreshTokenService: refresh token entity
    RefreshTokenService-->>AuthService: validated token
    AuthService->>JwtService: generateToken(userId, username, role)
    AuthService->>JwtService: generateRefreshToken(userId, username, role)
    AuthService->>RefreshTokenService: revokeRefreshToken(oldToken)
    AuthService->>RefreshTokenService: createRefreshToken(user)
    AuthService-->>AuthController: AuthResponse
    AuthController-->>Client: 200 OK + new tokens
```

---
---

# Feature: Logout e Revogacao de Tokens

> **Versao:** 2.0.0
> **Implementada em:** 2026-04-28
> **Status:** Concluida

---

## Resumo

Sistema de logout que invalida tanto o access token quanto o refresh token do usuario, impedindo reutilizacao.

**Motivacao:** Quando um usuario faz logout, os tokens emitidos devem ser invalidados imediatamente.
**Resultado:** Tokens sao adicionados a uma blacklist (access) ou revogados (refresh) ao fazer logout.

---

## Fluxo Principal

### 1. Ponto de Entrada

- **Tipo:** HTTP REST
- **Arquivo:** `auth-service/src/main/java/com/odevpedro/yugiohcollections/auth/adapter/in/rest/AuthController.java`
- **Rota:** `POST /auth/logout`
- **Autenticacao:** Bearer token (access token)

### 2. Regras de Negocio

| Regra | Descricao | Localizacao no Codigo |
|-------|-----------|----------------------|
| Access token blacklist | Token e adicionado a blacklist com hash SHA-256 | `TokenBlacklistService` |
| Refresh token revoke | Refresh token e marcado como revogado | `RefreshTokenService` |
| Blacklist expira | Entradas blacklist expiram quando token original expira | `TokenBlacklistEntity` |

### 3. Integracoes

| Servico | Operacao | Descricao |
|---------|----------|-----------|
| TokenBlacklistRepository | save() | Adiciona token a blacklist |
| RefreshTokenRepository | revokeAllByUserId() | Revoga todos os refresh tokens do usuario |

---

## Diagrama de Sequencia

```mermaid
sequenceDiagram
    actor Client
    participant AuthController
    participant AuthService
    participant TokenBlacklistService
    participant RefreshTokenService
    participant DB

    Client->>AuthController: POST /auth/logout
    Note over Client: Headers: Authorization: Bearer {accessToken}
    Note over Client: X-Refresh-Token: {refreshToken}
    AuthController->>AuthService: logout(accessToken, refreshToken)
    AuthService->>TokenBlacklistService: blacklistToken(accessToken)
    TokenBlacklistService->>TokenBlacklistService: hashToken(token)
    TokenBlacklistService->>DB: save blacklist_entry
    DB-->>TokenBlacklistService: saved
    TokenBlacklistService-->>AuthService: done
    AuthService->>RefreshTokenService: revokeRefreshToken(refreshToken)
    RefreshTokenService->>DB: UPDATE refresh_token SET revoked = true
    DB-->>RefreshTokenService: updated
    RefreshTokenService-->>AuthService: done
    AuthService-->>AuthController: void
    AuthController-->>Client: 204 No Content
```

---
---

# Feature: Validacao de Token via Blacklist

> **Versao:** 2.0.0
> **Implementada em:** 2026-04-28
> **Status:** Concluida

---

## Resumo

O `JwtAuthFilter` nos servicos verifica se o token esta na blacklist antes de permitir acesso, chamando o `auth-service` via Feign.

**Motivacao:** Token pode ser invalidado apos emissao (logout), entao deve ser verificado em cada requisicao.
**Resultado:** Servicos verificam token via Feign client antes de permitir acesso.

---

## Fluxo Principal

### 1. Ponto de Entrada

- **Tipo:** Filtro Spring (OncePerRequestFilter)
- **Arquivo:** `shared-domain/src/main/java/com/odevpedro/yugiohcollections/shared/security/JwtAuthFilter.java`
- **Autenticacao:** N/A (e o filtro que autentica)

### 2. Regras de Negocio

| Regra | Descricao | Localizacao no Codigo |
|-------|-----------|----------------------|
| Skip auth paths | Rotas publicas pulam validacao | `shouldNotFilter()` |
| Skip blacklist | auth-service nao valida blacklist de si mesmo | `skipBlacklistCheck` |
| Token hash | Tokens sao verificados por hash SHA-256 | `TokenBlacklistService.hashToken()` |

### 3. Integracoes

| Servico | Operacao | Timeout | Retry |
|---------|----------|---------|-------|
| auth-service | GET /internal/validate | 5s | 3 |

---

## Diagrama de Sequencia

```mermaid
sequenceDiagram
    actor Client
    participant JwtAuthFilter
    participant TokenValidationClient
    participant AuthService
    participant TokenBlacklistRepository
    participant DB

    Client->>JwtAuthFilter: GET /decks
    Note over Client: Authorization: Bearer {token}
    JwtAuthFilter->>JwtAuthFilter: shouldNotFilter(path)?
    JwtAuthFilter->>JwtAuthFilter: parse token
    JwtAuthFilter->>TokenValidationClient: validateToken(authHeader)
    TokenValidationClient->>AuthService: GET /internal/validate
    AuthService->>TokenBlacklistRepository: existsByTokenHash(hash)
    TokenBlacklistRepository->>DB: SELECT blacklist
    DB-->>TokenBlacklistRepository: result
    TokenBlacklistRepository-->>AuthService: boolean
    AuthService-->>TokenValidationClient: {"valid": true}
    TokenValidationClient-->>JwtAuthFilter: validation result
    JwtAuthFilter->>JwtAuthFilter: set SecurityContext
    JwtAuthFilter->>DeckController: proceed
    DeckController-->>Client: 200 OK + decks
```

---
---

# Feature: Validacao de Regras de Deck

> **Versao:** 1.0.0
> **Implementada em:** 2026-04-28
> **Status:** Concluida

---

## Resumo

Validacao das regras oficiais do Yu-Gi-Oh! TCG para composicao de decks, impedindo que o usuario adicione cartas que violem as regras do jogo.

**Motivacao:** Garantir que decks possam ser usados em torneios e jogos online.
**Resultado:** Erros claros sao retornados quando regras sao violadas.

---

## Regras Implementadas

| Regra | Valor | Descricao |
|-------|-------|-----------|
| Main Deck minimo | 40 | Minimo de cartas no Main Deck |
| Main Deck maximo | 60 | Maximo de cartas no Main Deck |
| Extra Deck maximo | 15 | Maximo de cartas no Extra Deck |
| Side Deck maximo | 15 | Maximo de cartas no Side Deck |
| Limite de copias | 3 | Maximo de 3 copias de cada carta |

---

## Fluxo Principal

### 1. Ponto de Entrada

- **Tipo:** HTTP REST
- **Arquivo:** `deck-service/src/main/java/com/odevpedro/yugiohcollections/deck/adapter/in/rest/DeckController.java`
- **Rota:** `POST /decks/{deckId}/cards`
- **Autenticacao:** Bearer token

### 2. Validacao de Entrada

| Campo | Tipo | Obrigatorio | Regra de validacao |
|-------|------|-------------|---------------------|
| cardId | Long | Sim | ID valido de carta |
| quantity | int | Sim | > 0 e <= 3 por carta |
| zone | String | Nao | MAIN, EXTRA ou SIDE (default: MAIN) |

### 3. Regras de Negocio

| Regra | Descricao | Localizacao no Codigo |
|-------|-----------|----------------------|
| Copias por carta | Max 3 copias de qualquer carta | `DeckValidator.validateAddCard()` |
| Main Deck size | 40-60 cartas | `DeckValidator.validateAddCard()` |
| Extra Deck size | Max 15 cartas | `DeckValidator.validateAddCard()` |
| Side Deck size | Max 15 cartas | `DeckValidator.validateAddCard()` |

### 4. Resposta de Erro

```json
{
  "statusCode": 400,
  "error": "DECK_VALIDATION_FAILED",
  "message": "Regras do deck violadas",
  "violations": [
    "Carta ID 46986414: nao e permitido ter mais de 3 copias no deck (atual: 2, tentando adicionar: 2)",
    "Main Deck nao pode ter mais de 60 cartas (atual: 58, apos adicionar: 60)"
  ],
  "timestamp": "2026-04-28T12:00:00"
}
```

### 5. Resposta do Endpoint /full

```json
{
  "id": 1,
  "name": "Meu Deck",
  "mainDeckSize": 45,
  "extraDeckSize": 10,
  "sideDeckSize": 15,
  "isValid": true,
  "validationErrors": [],
  "cards": [...]
}
```

---

## Diagrama de Sequencia

```mermaid
sequenceDiagram
    actor Client
    participant DeckController
    participant DeckApplicationService
    participant DeckValidator
    participant DeckRepository
    participant DB

    Client->>DeckController: POST /decks/1/cards
    Note over Client: {"cardId": 123, "quantity": 3, "zone": "MAIN"}
    DeckController->>DeckApplicationService: addCardToZone(ownerId, deckId, cardId, quantity, zone)
    DeckApplicationService->>DeckValidator: validateAddCard(deck, cardId, quantity, zone)
    DeckValidator->>DeckValidator: countCardCopies(deck, cardId)
    DeckValidator->>DeckValidator: getZoneSize(deck, zone)
    alt validacao OK
        DeckValidator-->>DeckApplicationService: OK
        DeckApplicationService->>Deck: addToMain(cardId)
        DeckApplicationService->>DeckRepository: save(deck)
        DeckRepository->>DB: UPDATE deck
        DB-->>DeckRepository: saved
        DeckRepository-->>DeckApplicationService: deck updated
        DeckApplicationService-->>DeckController: DeckView
        DeckController-->>Client: 200 OK
    else validacao falhou
        DeckValidator-->>DeckApplicationService: DeckValidationException
        DeckApplicationService-->>DeckController: 400 Bad Request
        DeckController-->>Client: {"error": "DECK_VALIDATION_FAILED", "violations": [...]}
    end
```

---
---

# Feature: Gerenciamento de Decks

> **Versao:** 1.0.0
> **Implementada em:** 2024
> **Status:** Concluida

---

## Resumo

CRUD completo de decks de cartas Yu-Gi-Oh! com validacao de regras do jogo (40-60 cartas no main deck, max 15 no extra/side, max 3 copias de cada carta).

**Motivacao:** Necessidade de gerenciar colecoes pessoais de decks.
**Resultado:** API REST para criar, editar, buscar e exportar decks no formato .ydk.

---

## Fluxo Principal

### 1. Ponto de Entrada

- **Tipo:** HTTP REST
- **Arquivo:** `deck-service/src/main/java/com/odevpedro/yugiohcollections/deck/adapter/in/rest/DeckController.java`
- **Rotas:** `POST /decks`, `GET /decks`, `GET /decks/{deckId}`, `GET /decks/{deckId}/full`, `POST /decks/{deckId}/cards`, `DELETE /decks/{deckId}/cards`, `DELETE /decks/{deckId}`, `GET /decks/{deckId}/export`
- **Autenticacao:** JWT obrigatorio

### 2. Validação de Entrada

| Campo | Tipo | Obrigatorio | Regra de validacao |
|-------|------|-------------|---------------------|
| name | String | Sim | Nao vazio, max 100 caracteres |

### 3. Regras de Negocio

| Regra | Descricao | Localizacao no Codigo |
|-------|-----------|----------------------|
| Main deck size | 40-60 cartas | `DeckValidator` |
| Extra deck size | 0-15 cartas | `DeckValidator` |
| Side deck size | 0-15 cartas | `DeckValidator` |
| Max copies | Max 3 copias de cada carta | `DeckValidator` |
| Isolation | Queries filtradas por ownerId | `DeckRepository` |

### 4. Integracoes

| Servico | Operacao | Timeout | Retry |
|---------|----------|---------|-------|
| card-service | GET /cards/internal/{id} via Feign | 5s | 3 |

### 5. Resposta Final

```json
{
  "id": 1,
  "name": "Meu Deck",
  "ownerId": "user-123",
  "mainDeckSize": 40,
  "extraDeckSize": 10,
  "sideDeckSize": 15
}
```

---

## Diagrama de Sequencia

```mermaid
sequenceDiagram
    actor Client
    participant DeckController
    participant DeckApplicationService
    participant DeckValidator
    participant CardFeignClient
    participant DeckRepository
    participant CardService

    Client->>DeckController: POST /decks
    DeckController->>DeckApplicationService: createDeck(userId, name)
    DeckApplicationService->>DeckValidator: validate(name)
    DeckValidator-->>DeckApplicationService: OK
    DeckApplicationService->>DeckRepository: save(deck)
    DeckRepository-->>DeckApplicationService: deck criado
    DeckApplicationService-->>DeckController: DeckView
    DeckController-->>Client: 201 Created

    Client->>DeckController: POST /decks/{id}/cards
    DeckController->>DeckApplicationService: addCard(userId, deckId, cardId, quantity)
    DeckApplicationService->>CardFeignClient: findById(cardId)
    CardFeignClient->>CardService: GET /cards/internal/{id}
    CardService-->>CardFeignClient: Card data
    CardFeignClient-->>DeckApplicationService: Card
    DeckApplicationService->>DeckValidator: validateCardAddition(deck, card, quantity)
    DeckValidator-->>DeckApplicationService: OK
    DeckApplicationService->>DeckRepository: save(deck)
    DeckRepository-->>DeckApplicationService: deck atualizado
    DeckApplicationService-->>DeckController: DeckView
    DeckController-->>Client: 200 OK
```

---
---

# Feature: Busca de Cartas

> **Versao:** 1.0.0
> **Implementada em:** 2024
> **Status:** Concluida

---

## Resumo

Busca de cartas do catalogo oficial Yu-Gi-Oh! via YGOPRODeck API, com cache em memoria e circuit breaker para resiliencia.

**Motivacao:** Necessidade de buscar cartas oficiais para adicionar aos decks.
**Resultado:** API REST com busca por nome, tipo e descricao, cache inteligente e resiliencia.

---

## Fluxo Principal

### 1. Ponto de Entrada

- **Tipo:** HTTP REST
- **Arquivo:** `card-service/src/main/java/com/odevpedro/yugiohcollections/card/adapter/in/rest/CardController.java`
- **Rotas:** `GET /cards` (busca paginada), `GET /cards/internal/{id}`, `GET /cards/internal?ids=`
- **Autenticacao:** `/cards` publica, `/cards/internal` requer token

### 3. Regras de Negocio

| Regra | Descricao | Localizacao no Codigo |
|-------|-----------|----------------------|
| Cache | Resultados em cache por 1 hora | `CacheConfig` |
| Circuit Breaker | Abre circuito apos 5 falhas | `Resilience4jConfig` |
| Retry | 3 tentativas com backoff | `Resilience4jConfig` |

### 4. Integracoes

| Servico | Operacao | Timeout | Retry |
|---------|----------|---------|-------|
| YGOPRODeck API | GET /cardinfo.php | 10s | 3 |

---

## Diagrama de Sequencia

```mermaid
sequenceDiagram
    actor Client
    participant CardController
    participant SearchCardsUseCase
    participant ExternalCardQueryPort
    participant Cache
    participant CircuitBreaker
    participant YGOPROAPI

    Client->>CardController: GET /cards?name=Dark%20Magician
    CardController->>SearchCardsUseCase: search(name, pageable)
    SearchCardsUseCase->>Cache: get(key)
    Cache-->>SearchCardsUseCase: cache miss
    SearchCardsUseCase->>ExternalCardQueryPort: findByName(name)
    ExternalCardQueryPort->>CircuitBreaker: execute()
    CircuitBreaker->>YGOPROAPI: GET /cardinfo.php?name=Dark%20Magician
    YGOPROAPI-->>CircuitBreaker: card data
    CircuitBreaker-->>ExternalCardQueryPort: result
    ExternalCardQueryPort-->>SearchCardsUseCase: cards
    SearchCardsUseCase->>Cache: put(key, cards)
    SearchCardsUseCase-->>CardController: Page<CardResponseDTO>
    CardController-->>Client: 200 OK + paginated cards
```

---
---

# Feature: Criacao de Cartas Customizadas

> **Versao:** 1.0.0
> **Implementada em:** 2024
> **Status:** Concluida

---

## Resumo

Sistema de criacao de cartas customizadas com validacao de regras do jogo, publicacao de eventos via Kafka para validacao assincrona.

**Motivacao:** Jogadores queriam criar cartas personalizadas.
**Resultado:** API REST para criar cartas com validacao automatica de stats e publicacao para validacao por IA.

---

## Fluxo Principal

### 1. Ponto de Entrada

- **Tipo:** HTTP REST
- **Arquivo:** `card-creator-service/src/main/java/com/odevpedro/yugiohcollections/creator/adapter/in/rest/CustomCardController.java`
- **Rotas:** `POST /custom-cards`, `GET /custom-cards/{id}`, `GET /custom-cards`
- **Autenticacao:** JWT obrigatorio

### 3. Regras de Negocio

| Regra | Descricao | Localizacao no Codigo |
|-------|-----------|----------------------|
| ATK/DEF | Entre 0 e 5000 | `CustomCard` |
| Level | Entre 1 e 12 | `CustomCard` |
| Nome | Max 255 caracteres | `CustomCard` |
| Efeito | Max 2000 caracteres | `CustomCard` |

### 4. Integracoes

| Servico | Operacao | Topico |
|---------|----------|--------|
| Kafka | Producer | `card.created` |
| Kafka | Consumer | `card.validated` |

---

## Diagrama de Sequencia

```mermaid
sequenceDiagram
    actor Client
    participant CustomCardController
    participant CustomCardService
    participant CustomCardRepository
    participant KafkaProducer
    participant KafkaConsumer
    participant Validator

    Client->>CustomCardController: POST /custom-cards
    CustomCardController->>CustomCardService: create(userId, request)
    CustomCardService->>CustomCardService: valida stats (ATK, DEF, Level)
    CustomCardService->>CustomCardRepository: save(card)
    CustomCardRepository-->>CustomCardService: card criada (PENDING)
    CustomCardService->>KafkaProducer: send("card.created", event)
    KafkaProducer-->>Validator: Consome evento
    Validator->>Validator: valida balanceamento
    Validator-->>KafkaConsumer: card.validated (APPROVED/REJECTED)
    KafkaConsumer->>CustomCardRepository: atualiza status
    CustomCardController-->>Client: 201 Created + card
```

---
---

# Feature: Geracao de Proxy PDF

> **Versao:** 1.0.0
> **Implementada em:** 2024
> **Status:** Concluida

---

## Resumo

Geracao de PDF com imagens das cartas do deck para impressao de proxies em torneios.

**Motivacao:** Jogadores precisam de proxies impressos para cartas que nao tem.
**Resultado:** API REST que gera PDF com layout de carta por pagina.

---

## Fluxo Principal

### 1. Ponto de Entrada

- **Tipo:** HTTP REST
- **Arquivo:** `proxy-service/src/main/java/com/odevpedro/yugiohcollections/proxy/adapter/in/rest/ProxyController.java`
- **Rota:** `GET /proxy/{deckId}`
- **Autenticacao:** JWT obrigatorio

### 4. Integracoes

| Servico | Operacao | Timeout |
|---------|----------|---------|
| deck-service | GET /decks/{deckId}/full via Feign | 30s |

---

## Diagrama de Sequencia

```mermaid
sequenceDiagram
    actor Client
    participant ProxyController
    participant ProxyPdfService
    participant DeckFeignClient
    participant DeckService
    participant PdfGenerator

    Client->>ProxyController: GET /proxy/{deckId}
    ProxyController->>ProxyPdfService: generateProxyPdf(deckId)
    ProxyPdfService->>DeckFeignClient: getDeckWithCards(deckId)
    DeckFeignClient->>DeckService: GET /decks/{deckId}/full
    DeckService-->>DeckFeignClient: deck + cards
    DeckFeignClient-->>ProxyPdfService: deck data
    ProxyPdfService->>PdfGenerator: generate(deck)
    PdfGenerator-->>ProxyPdfService: PDF bytes
    ProxyPdfService-->>ProxyController: PDF
    ProxyController-->>Client: 200 OK + PDF (attachment)
```

---
---

# Feature: Comunidade e Desafios

> **Versao:** 1.0.0
> **Implementada em:** 2024
> **Status:** Concluida

---

## Resumo

Sistema de geolocalizacao de jogadores e desafios de duelo, usando PostGIS para busca por raio e Kafka para notificacoes.

**Motivacao:** Jogadores queriam encontrar oponentes cercanos para duelar.
**Resultado:** API REST para registro de jogadores, busca por proximidade e sistema de desafios.

---

## Fluxo Principal

### 1. Ponto de Entrada

- **Tipo:** HTTP REST
- **Arquivos:**
  - `community-service/.../PlayerController.java`
  - `community-service/.../ChallengeController.java`
- **Rotas Players:** `POST /players`, `PATCH /players/me/status`, `GET /players/nearby`
- **Rotas Challenges:** `POST /challenges`, `PATCH /challenges/{id}`, `GET /challenges/pending`
- **Autenticacao:** JWT obrigatorio

### 3. Regras de Negocio

| Regra | Descricao | Localizacao no Codigo |
|-------|-----------|----------------------|
| Busca por raio | PostGIS ST_DWithin | `PlayerRepository` |
| Expiracao | Desafios pendentes expiram em 30 min | `@Scheduled` |
| Status | AVAILABLE, BUSY, OFFLINE | `DuelStatus` |

### 4. Integracoes

| Servico | Operacao | Topico |
|---------|----------|--------|
| Kafka | Producer | `desafio.recebido`, `desafio.aceito`, `desafio.expirado` |
| Kafka | Consumer | `duel.encerrado` |

---

## Diagrama de Sequencia

```mermaid
sequenceDiagram
    actor Client
    participant PlayerController
    participant ChallengeController
    participant PlayerService
    participant ChallengeService
    participant PlayerRepository
    participant KafkaProducer
    participant KafkaConsumer
    participant Scheduler

    Client->>PlayerController: POST /players
    PlayerController->>PlayerService: registerOrUpdate(userId, displayName, lat, lng, platforms)
    PlayerService->>PlayerRepository: save(player)
    PlayerRepository->>PlayerRepository: ST_SetSRID(location, 4326)
    PlayerRepository-->>PlayerService: player registrado
    PlayerService-->>PlayerController: Player
    PlayerController-->>Client: 200 OK

    Client->>PlayerController: GET /players/nearby?lat=-23.5&lng=-46.6&radiusKm=10
    PlayerController->>PlayerService: findNearby(lat, lng, radiusKm, status)
    PlayerService->>PlayerRepository: findNearby(lat, lng, radiusKm)
    PlayerRepository->>PlayerRepository: ST_DWithin(location, ST_MakePoint(lng, lat), radiusKm * 1000)
    PlayerRepository-->>PlayerService: jogadores proximos
    PlayerService-->>PlayerController: List<Player>
    PlayerController-->>Client: 200 OK

    Client->>ChallengeController: POST /challenges
    ChallengeController->>ChallengeService: sendChallenge(challengerId, targetId, deckId, message)
    ChallengeService->>ChallengeService: valida que alvo esta AVAILABLE
    ChallengeService->>ChallengeRepository: save(challenge)
    ChallengeService->>KafkaProducer: send("desafio.recebido", event)
    KafkaProducer-->>Client: desafio
    ChallengeController-->>Client: 200 OK + challenge

    Scheduler->>ChallengeService: expiraChallengesPendentes()
    ChallengeService->>ChallengeRepository: findPendingOlderThan(30min)
    ChallengeRepository-->>ChallengeService: desafios expirados
    ChallengeService->>ChallengeRepository: saveAll(expired)
    ChallengeService->>KafkaProducer: send("desafio.expirado", event)
```

---
---

## Historico

| Data | Mudanca |
|------|---------|
| 2026-04-28 | Adicionada documentacao de ApiRoutes — centralizacao de rotas |
| 2024 | Criacao do documento com todas as features |