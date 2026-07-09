# ADR-002: Arquitetura hexagonal

Status: Aceito

## Contexto
O monorepo cresceu com multiplos dominios e integracoes externas: banco, Kafka, Feign, JWT, geracao de proxy e utilitarios compartilhados.
Sem um limite claro, a regra de negocio tende a se misturar com adaptadores de entrada/saida.

## Decisao
Cada servico segue arquitetura hexagonal, separando:
- dominio e regras de negocio
- casos de uso e orquestracao
- adaptadores de entrada
- adaptadores de saida

## Consequencias
- Facilita testes unitarios e de integracao por isolar o dominio.
- Permite trocar adaptadores sem reescrever a regra de negocio.
- Aumenta a quantidade de classes e a necessidade de contratos bem definidos.

