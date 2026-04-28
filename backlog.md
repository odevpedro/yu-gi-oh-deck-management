# Backlog — Yu-Gi-Oh! Deck Management

> Registro vivo do progresso do projeto. Atualizado a cada mudanca de estado de uma funcionalidade.
> **Ultima atualizacao:** 2026-04-28

---

## Sobre o Projeto

Sistema de gerenciamento de colecoes e decks de cartas Yu-Gi-Oh!, construdo com arquitetura hexagonal em microservicos independentes que se comunicam via OpenFeign e Kafka.

**Versao atual:** `0.2.0`
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
| `XS` `S` `M` `L` `XL` | Estimativa de complexidade |

---

## Em Andamento

> Features atualmente sendo desenvolvidas. Idealmente, maximo de 2–3 itens simultaneos.

| ID | Feature | Prioridade | Estimativa |
|----|---------|------------|------------|

---

## Pendentes

> Ordenadas por prioridade. Itens de P0 e P1 devem entrar em "Em Andamento" primeiro.

### Autenticacao e Seguranca

| ID | Feature | Prioridade | Estimativa |
|----|---------|------------|------------|
| AUTH-001 | Autenticacao centralizada com JWT e refresh token | P1 | L |
| AUTH-002 | Logout e revogacao de tokens | P2 | M |
| AUTH-003 | Filtro JWT compartilhado via shared-domain | P1 | M |

### Core Features

| ID | Feature | Prioridade | Estimativa |
|----|---------|------------|------------|
| CORE-001 | Validacao de regras de deck (40-60 main, max 15 extra/side, max 3 copias) | P1 | M |
| CORE-002 | Import de deck via arquivo .ydk | P2 | M |
| CORE-003 | Sincronizacao de deck via Kafka entre servicos | P2 | L |

### Servicos Planejados

| ID | Feature | Prioridade | Estimativa |
|----|---------|------------|------------|
| SVCE-001 | konami-validator-service — validacao de balanceamento via regras configuraveis | P2 | XL |
| SVCE-002 | duel-service (repo separado) — motor de duelo integrado ao ocgcore (C++ + Lua) | P3 | XL |

### Infraestrutura

| ID | Feature | Prioridade | Estimativa |
|----|---------|------------|------------|
| INFRA-001 | Elasticsearch — busca full-text de cartas por descricao de efeito | P2 | L |
| INFRA-002 | API Gateway com Spring Cloud Gateway | P2 | M |
| INFRA-003 | Prometheus + Grafana para metricas | P3 | M |
| INFRA-004 | Zipkin para distributed tracing | P3 | M |

### Testes

| ID | Feature | Prioridade | Estimativa |
|----|---------|------------|------------|
| TEST-001 | Testes unitarios com JUnit | P1 | L |
| TEST-002 | Testes de integracao com Testcontainers | P2 | L |

---

## Concluidas

> Features finalizadas com suas respectivas datas de conclusao e links de referencia.

### Arquitetura

| ID | Feature | Data | Referencia |
|----|---------|------|------------|
| ARCH-001 | Arquitetura Hexagonal (Ports & Adapters) | 2024 | hex-arch |
| ARCH-002 | Centralizacao de rotas em ApiRoutes | 2026-04-28 | - |

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

### Features de Cards Customizados

| ID | Feature | Data | Referencia |
|----|---------|------|------------|
| CARD-001 | Criacao de cartas customizadas | 2024 | CustomCardController |
| CARD-002 | Validacao de stats (ATK/DEF 0-5000, Level 1-12) | 2024 | CustomCard domain |
| CARD-003 | Validacao de texto (nome max 255, efeito max 2000) | 2024 | CustomCard domain |
| CARD-004 | Publicacao de eventos via Kafka (card.created) | 2024 | KafkaProducer |

### Features de Comunidade

| ID | Feature | Data | Referencia |
|----|---------|------|------------|
| COMM-001 | Registro de jogadores com geolocalizacao | 2024 | PlayerController |
| COMM-002 | Busca de jogadores proximos via PostGIS | 2024 | PlayerService |
| COMM-003 | Sistema de desafios de duelo | 2024 | ChallengeController |
| COMM-004 | Expiracao automatica de desafios | 2024 | @Scheduled |

---

## Bugs Conhecidos

> Problemas identificados que ainda nao foram corrigidos.

| ID | Descricao | Severidade | Reportado em |
|----|-----------|------------|--------------|

---

## Notas e Decisoes Pendentes

> Pontos em aberto que precisam de decisao antes de serem desenvolvidos.

- [ ] Decidir estrategia de sincronizacao entre deck-service e card-service
- [ ] Definir formato de eventos Kafka entre servicos
- [ ] Configurar CORS para permitir acesso de origens externas
- [ ] Implementar rate limiting no API Gateway

---

## Historico de Versoes

| Versao | Data | Principais entregas |
|--------|------|---------------------|
| `0.2.0` | 2026-04-28 | Centralizacao de rotas em ApiRoutes, community-service e auth-service implementados |
| `0.1.0` | 2024 | MVP com card-service, deck-service, proxy-service, card-creator-service |