# ADR-004: Redis como state store

Status: Aceito

## Contexto
O duelo precisa de estado temporal, leitura rapida e possivel expurgo/expiracao de informacao mutavel.
Guardar tudo apenas em banco relacional simplifica o inicio, mas tende a piorar a latencia do estado em jogo.

## Decisao
Usaremos Redis como state store para estado efemero e altamente mutavel quando o fluxo de jogo exigir.
O banco relacional permanece como fonte de verdade para dados persistentes e historicos.

## Consequencias
- Melhora acesso a estado temporario e padroes de jogo em tempo real.
- Introduz uma dependencia extra de infra.
- Exige politicas de expiracao, sincronizacao e recuperacao mais claras.

