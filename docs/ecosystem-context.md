# Contexto Integrado — Yu-Gi-Oh! Collections

> Visão geral do ecossistema de aplicações para gerenciamento de duelos Yu-Gi-Oh!

---

## Visão Geral

O ecossistema é composto por 3 projetos principais:

| Projeto | Repositório | Descrição |
|---------|-------------|-----------|
| **duel-service** | github.com/odevpedro/duel-service | Motor de duelos em tempo real com WebSocket |
| **yu-gi-oh-deck-management** | github.com/odevpedro/yu-gi-oh-deck-management | Backend de microsserviços (deck, cards, auth) |
| **yu-gi-oh-deck-management-front-end** | github.com/odevpedro/yu-gi-oh-deck-management-front-end | Frontend React |

---

## Arquitetura do Sistema

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              FRONT-END                                     │
│                         (React + Canvas 2D)                                 │
│                duel-react / yugioh-duel-react                             │
└───────────────────────────────────┬─────────────────────────────────────────┘
                                    │ HTTP / WebSocket
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           BACKEND SERVICES                                  │
│                                                                             │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐          │
│  │  auth-service   │  │  deck-service   │  │ card-service    │          │
│  │  :8081          │  │  :8082           │  │ :8083           │          │
│  └────────┬─────────┘  └────────┬─────────┘  └──────────────────┘          │
│           │                     │                                            │
│           │    OpenFeign       │                                            │
│           └─────────┬──────────┘                                            │
│                     │                                                       │
│           ┌─────────▼──────────┐                                            │
│           │ community-service  │ ───▶ PostgreSQL + PostGIS               │
│           │ :8080              │                                            │
│           └─────────┬──────────┘                                            │
│                     │                                                       │
└─────────────────────│───────────────────────────────────────────────────────┘
                      │
                      │ REST API + WebSocket
                      ▼
┌─────────────────────��───────────────────────────────────────────────────────┐
│                         DUEL-SERVICE                                        │
│                      Java 21 + Spring Boot                                 │
│                    WebSocket + Redis + JNI                                 │
│                                                                             │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐          │
│  │  REST API       │  │  WebSocket       │  │  ocgcore JNI     │          │
│  │  :8084/api/*    │  │  :8084/ws        │  │  (motor C++)     │          │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘          │
│                                                                             │
│  Persistência:                                                              │
│    - Redis (estado atual do duelo - 24h TTL)                               │
│    - H2/PostgreSQL (histórico de duelos)                                   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Stack Tecnológica

### duel-service

| Componente | Tecnologia |
|------------|------------|
| Runtime | Java 21 |
| Framework | Spring Boot 3.2 |
| WebSocket | STOMP over SockJS |
| Autenticação | JWT (mesma chave do auth-service) |
| Persistência | Redis + JPA (H2 dev / PostgreSQL prod) |
| Game Engine | ocgcore (C++) via JNI |
| Build | Gradle |

### yu-gi-oh-deck-management (Backend)

| Componente | Tecnologia |
|------------|------------|
| Runtime | Java 17 |
| Framework | Spring Boot 3.2 |
| Build | Gradle |
| Database | PostgreSQL |
| Message Broker | Kafka |
| API Client | OpenFeign |

**Serviços:**
- auth-service (JWT, refresh token, blacklist)
- deck-service (CRUD, validação, export .ydk)
- card-service (YGOPRODeck API)
- card-creator-service (cartas customizadas)
- community-service (geolocalização, desafios)
- shared-domain (enums, filtros, configurações)

### yu-gi-oh-deck-management-front-end

| Componente | Tecnologia |
|------------|------------|
| Framework | React 18 + Vite 5 |
| Gráficos | Canvas 2D |
| API | YGOPRODeck (cartas oficiais) |
| Estado | React Context |

---

## Fluxo de Dados

### 1. Criação de Duelo

```
Frontend                    Community Service              Duel Service
    │                              │                            │
    │──── POST /challenges ──────▶│                            │
    │                              │                            │
    │                              │──── POST /api/duels ──────▶│
    │                              │    (playerAId, playerBId,  │
    │                              │     playerADeckId,         │
    │                              │     playerBDeckId)         │
    │                              │                            │
    │◀────── Response { duelId } ─────────────────────────────│
    │                              │                            │
``` 

O duel-service busca os decks dos jogadores via Feign no deck-service.

### 2. Conexão WebSocket

```
Frontend                    Duel Service (WebSocket)
    │                              │
    │──── CONNECT /ws ────────────▶│
    │     (JWT no header)          │
    │                              │
    │◀── CONNECTED ───────────────│
    │                              │
    │──── SUBSCRIBE ──────────────▶│
    │     /topic/duel/{duelId}    │
    │                              │
    │◀── Estado inicial ──────────│
```

### 3. Gameplay em Tempo Real

```
Frontend                    Duel Service                     ocgcore
    │                              │                              │
    │──── /app/duel.action ──────▶│                              │
    │     { SUMMON, cardId, zone }│                              │
    │                              │──── processAction() ────────▶│
    │                              │                              │
    │◀── Estado atualizado ───────│◀── Novo estado ─────────────│
    │                              │                              │
    │◀── /topic/duel/{duelId} ────│ (broadcast para oponente)  │
```

---

## Autenticação

### JWT Flow

1. **Login** → auth-service retorna access_token + refresh_token
2. **APIs REST** → Header `Authorization: Bearer <token>`
3. **WebSocket** → Header `Authorization: Bearer <token>` no handshake STOMP

### Configuração

O duel-service compartilha a mesma chave secreta do auth-service:

```yaml
# duel-service application.yml
jwt:
  secret: ${JWT_SECRET}
  expirationMs: 3600000  # 1 hora
  skip-blacklist-check: true  # dev only
```

---

## Endpoints

### duel-service

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/duels` | Criar duelo |
| GET | `/api/duels/{id}` | Estado do duelo |
| GET | `/api/duels/history` | Histórico de duelos |
| GET | `/api/duels/history/player/{id}` | Histórico por jogador |
| WS | `/ws` | WebSocket STOMP |
| SUB | `/topic/duel/{id}` | Estado do duelo |
| SUB | `/topic/duel/{id}/over` | Fim de duelo |
| PUB | `/app/duel.action` | Executar ação |
| PUB | `/app/duel.phase` | Avançar fase |

### deck-service (via Feign no duel-service)

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/decks/{id}` | Deck do jogador |
| GET | `/api/decks/{id}/cards` | Cartas do deck |
| POST | `/api/decks` | Criar deck |

---

## Como Rodar

### 1. Backend (deck-management)

```bash
cd yu-gi-oh-deck-management

# Subir serviços (docker-compose)
docker-compose up -d

# Rodar local (gradle)
./gradlew :auth-service:bootRun
./gradlew :deck-service:bootRun
./gradlew :community-service:bootRun
```

### 2. duel-service

```bash
cd duel-service

# Development (in-memory + stub)
./gradlew bootRun

# Production (Redis)
export REDIS_HOST=localhost
export JWT_SECRET=sua-chave-secreta
./gradlew bootRun
```

### 3. Frontend

```bash
cd yu-gi-oh-deck-management-front-end/yugioh-duel-react/yugioh-duel-react

npm install
npm run dev
```

---

## Variáveis de Ambiente

### duel-service

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SERVER_PORT` | 8084 | Porta do serviço |
| `REDIS_HOST` | localhost | Host do Redis |
| `REDIS_PORT` | 6379 | Porta do Redis |
| `JWT_SECRET` | - | Chave secreta para JWT |
| `DECK_SERVICE_URL` | http://localhost:8082 | URL do deck-service |

### deck-service

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SERVER_PORT` | 8082 | Porta do serviço |
| `DATABASE_URL` | - | URL do PostgreSQL |

---

## Status das Features

### duel-service

- [x] Criação de duelos via REST API
- [x] WebSocket STOMP para tempo real
- [x] Integração JNI com ocgcore
- [x] Gerenciamento de fases
- [x] Sistema de ações (SUMMON, ATTACK, SPELL, SET)
- [x] Autenticação JWT em WebSocket
- [x] Disconnect handling (timeout 3min + WO)
- [x] Persistência Redis
- [x] Histórico de duelos (JPA)
- [x] Integração com deck-service via Feign
- [x] Testes unitários

### Frontend

- [x] Campo de duelo React (drag-drop)
- [x] Fases de turno (overlay)
- [x] Invocação normal (Canvas effects)
- [x] Sistema de ataque (seta animada)
- [x] Ativação de magias/armadilhas
- [x] Visualizador de deck
- [x] Painel de contexto (hover)
- [ ] IA do oponente (P0)
- [ ] Error boundaries (P1)
- [ ] WebSocket (implementar WebSocketEngine)

---

## Integração Frontend ↔ Duel Service

O frontend já tem arquitetura de adapter preparada:

```javascript
// engine/index.js
import { LocalEngine } from './LocalEngine'
// Para WebSocket, importar WebSocketEngine
import { WebSocketEngine } from './WebSocketEngine'

export const engine = new LocalEngine() //目前的
// export const engine = new WebSocketEngine() // futuro
```

Para completar a integração:

1. Criar `WebSocketEngine` implementando `DuelEngineAdapter`
2. Conectar ao WebSocket com JWT
3. Enviar ações via STOMP
4. Receber estado via `/topic/duel/{id}`
5. Substituir `LocalEngine` por `WebSocketEngine` em `index.js`

---

## Glossário

| Termo | Definição |
|-------|-----------|
| **ocgcore** | Motor C++ de regras Yu-Gi-Oh! |
| **WO** | Walkover - vitória por ausência do oponente |
| **STOMP** | Protocolo de mensagens para WebSocket |
| **SockJS** | WebSocket com fallback para browsers antigos |
| **JWT** | JSON Web Token para autenticação |
| **Feign** | Cliente HTTP declarativo para microsserviços |

---

## Links Úteis

- [duel-service README](./duel-service/README.md)
- [deck-management README](./yu-gi-oh-deck-management/README.md)
- [Frontend](./yu-gi-oh-deck-management-front-end/)
- [YGOPRODeck API](https://db.ygoprodeck.com/api-guide/)
- [ocgcore](https://github.com/edo9300/ygopro-core)