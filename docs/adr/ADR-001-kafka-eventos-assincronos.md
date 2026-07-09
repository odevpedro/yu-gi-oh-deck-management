# ADR-001: Kafka para eventos assincronos

Status: Aceito

## Contexto
O ecossistema precisa propagar eventos entre servicos sem acoplamento direto.
O fluxo mais sensivel e o de duelo: `community-service` precisa reagir ao encerramento de duelo publicado pelo `duel-service`, e outros fluxos tambem precisam de notifica/consumo assíncrono.

## Decisao
Adotamos Kafka como barramento de eventos asincronos entre os servicos do ecossistema.
Eventos relevantes sao publicados em topicos sem dependencias diretas de request/response entre processos.

## Consequencias
- Reduz acoplamento entre servicos.
- Permite processamento eventual de fluxos como desafio, inicio de duelo e encerramento.
- Introduz necessidade de serializacao consistente, observabilidade e retry/compensacao.
- Exige infraestrutura adicional durante desenvolvimento e testes.

