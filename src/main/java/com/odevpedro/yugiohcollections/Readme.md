
# Yu-Gi-Oh Deck Manager - Arquitetura de Microserviços

Este projeto é uma aplicação distribuída composta por micro-serviços independentes que colaboram entre si para permitir a criação, validação e organização de decks personalizados de Yu-Gi-Oh

---

## Micro-serviços

### 1. `card-service`

Gerencia todas as operações relacionadas a cartas.

- Consulta cartas da API pública YGOProDeck
- Cria cartas customizadas
- Persiste cartas no banco de dados local
- Fornece cartas para os decks


### 2. `validator-service`

Responsável por validar a integridade e viabilidade de cartas customizadas.

- Aplica regras de balanceamento
- Invalida cartas quebradas ou com atributos excessivos
- Se comunica com o card-service via mensageria


### 3. `deck-service`

Gerencia a estrutura dos decks de usuários.

- Criação e edição de decks (Main, Side, Extra)
- Adição e remoção de cartas
- Aplica regras de composição de deck (limites, tipo, número)


### 4. `analytics-service` *(opcional)*

Oferece estatísticas e insights sobre o uso de cartas e decks.

- Gera gráficos e relatórios de frequência
- Pode sugerir arquétipos e ajustes de estratégia
- Integra com deck-service e card-service via eventos


### 5. `notification-service` *(futuro)*

Envia notificações e alertas para os usuários.

- Informa sobre validação de cartas
- Confirmações de criação de deck
- Pode incluir integração com serviços externos (e-mail, push, etc.)


---

## Comunicação entre Serviços


- **Mensageria**: Para eventos como criação de carta, atualização de deck, validação, etc.
- **REST APIs**: Para operações síncronas como buscas, cadastro, leitura.

---

## Tecnologias utilizadas

- Java 17+
- Spring Boot 3
- Spring Cloud OpenFeign
- Spring Data JPA
- Mensageria (Kafka/RabbitMQ)
- H2 / PostgreSQL
- Arquitetura Hexagonal (Ports and Adapters)

---

## Estrutura da Solução

Cada microserviço é um projeto isolado com seu próprio ciclo de vida, base de código e responsabilidades.

```bash
yu-gi-oh-deck-manager/
├── card-service/
├── deck-service/
├── konami-service/
├── analytics-service/
└── notification-service/
```

---

**Documentação e diagramas adicionais serão exibidos aqui futuramente.**
