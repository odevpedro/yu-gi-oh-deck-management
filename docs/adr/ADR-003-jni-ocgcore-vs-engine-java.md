# ADR-003: JNI com ocgcore versus engine Java pura

Status: Aceito

## Contexto
O duel-service pode evoluir com uma engine Java propia ou com integracao nativa ao `ocgcore`.
O backlog do ecossistema ja reconhece que a parte de jogo precisa de comportamento minimamente jogavel antes de refinarmos a implementacao final.

## Decisao
Mantemos a engine de duelo em Java como fonte principal de negocio e usamos JNI apenas onde houver vantagem pratica clara para reproduzir regras complexas ja consolidadas.

## Consequencias
- Reduz o custo inicial de manter o fluxo jogavel.
- Evita trazer dependencia nativa cedo demais para toda a aplicacao.
- Mantem a porta aberta para JNI em trechos especificos sem acoplar o dominio inteiro a runtime nativa.

