# Observability — Execucao Codex AtendePro

Esta camada registra o trabalho do projeto como trilha operacional. Ela nao substitui logs de aplicacao, Actuator, Prometheus ou Docker; ela observa a execucao das tasks, releases, validacoes, falhas e decisoes de engenharia.

## Objetivo

- Manter historico estruturado por release e task.
- Registrar falhas com causa, impacto e acao tomada.
- Gerar metricas simples de execucao sem depender de ferramenta externa.
- Produzir checklist automatico por release a partir de `docs/RELEASE_STATUS.yaml`.
- Facilitar retomada de contexto entre sessoes e agentes.

## Arquivos

- `docs/codex/observability/events.jsonl`: eventos estruturados de execucao.
- `docs/codex/observability/failures.jsonl`: falhas, bloqueios e riscos relevantes.
- `docs/codex/observability/reports/`: relatorios gerados por release.
- `scripts/codex-observability.ps1`: comandos locais para registrar eventos, falhas, relatorios e checklists.

## Evento estruturado

Cada linha de `events.jsonl` deve ser um JSON independente.

Campos recomendados:

- `timestamp`: data/hora ISO 8601.
- `release`: release relacionada, por exemplo `R18`.
- `task`: task relacionada, por exemplo `TASK-NUTRI-015`.
- `status`: `INICIADA`, `EM_ANDAMENTO`, `CONCLUIDA`, `BLOQUEADA`, `FALHOU` ou `VALIDADA`.
- `area`: `produto`, `arquitetura`, `backend`, `frontend`, `mobile`, `qa`, `devops`, `docs`, `governanca`.
- `summary`: resumo curto do que aconteceu.
- `commands`: comandos relevantes executados.
- `result`: resultado objetivo.

## Falha estruturada

Cada linha de `failures.jsonl` deve registrar uma falha recuperavel ou bloqueio real.

Campos recomendados:

- `timestamp`: data/hora ISO 8601.
- `release`: release afetada.
- `task`: task afetada.
- `severity`: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.
- `failureType`: `build`, `test`, `docker`, `runtime`, `integration`, `scope`, `context`, `git`, `unknown`.
- `cause`: causa observada.
- `impact`: impacto pratico.
- `action`: acao tomada ou proxima acao.
- `resolved`: `true` ou `false`.

## Comandos

Registrar evento:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\codex-observability.ps1 record -Release R18 -Task TASK-NUTRI-015 -Status INICIADA -Area backend -Summary "Inicio da implementacao" -Command "mvn test" -Result "em andamento"
```

Registrar falha:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\codex-observability.ps1 fail -Release R18 -Task TASK-NUTRI-015 -Severity HIGH -FailureType test -Cause "Teste falhou no contrato X" -Impact "Task bloqueada" -Action "Corrigir mapper e repetir validacao"
```

Gerar relatorio:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\codex-observability.ps1 report -Release R18
```

Gerar checklist automatico da release:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\codex-observability.ps1 checklist -Release R18
```

## Rotina por task

1. Registrar `INICIADA` ao comecar.
2. Registrar eventos relevantes de implementacao e validacao.
3. Registrar falhas somente quando houver causa util para retomada.
4. Gerar checklist da release antes de marcar a task como concluida.
5. Gerar relatorio da release ao final de blocos de autopilot.
6. Registrar `CONCLUIDA`, `BLOQUEADA` ou `FALHOU` antes do commit.

## Criterio de qualidade

Uma task bem observada deve responder rapidamente:

- O que foi feito?
- Em qual release/task?
- Quais comandos foram executados?
- O que falhou?
- Qual foi a causa?
- Qual e a proxima acao?
- O status da release confere com o backlog?
