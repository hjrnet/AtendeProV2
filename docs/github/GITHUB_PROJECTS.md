# GitHub Projects — AtendePro Roadmap

Esta configuracao cria a visao web de acompanhamento curto prazo do AtendePro no GitHub Projects.

## Objetivo

- Transformar releases e `TASK-*` em uma visao web acompanhavel.
- Conectar backlog, commits, branches, issues, milestones e PRs.
- Manter o reposititorio como fonte tecnica e o GitHub Project como visao executiva.

## Project recomendado

- Nome: `AtendePro Roadmap`
- Repositorio: `hjrnet/AtendeProV2`
- Escopo inicial: releases recentes sincronizadas pelo backlog local.
- URL: `https://github.com/users/hjrnet/projects/4`

## Colunas/status

- `Backlog`
- `Ready`
- `In Progress`
- `Review`
- `Testing`
- `Done`
- `Blocked`

## Labels

- `release/R18` ate `release/R27`, conforme releases informadas ao script.
- `vertical/nutri`
- `vertical/beauty`
- `vertical/growth`
- `area/backend`
- `area/frontend`
- `area/mobile`
- `area/qa`
- `area/devops`
- `area/docs`
- `priority/P0`
- `priority/P1`
- `priority/P2`
- `status/blocked`
- `codex/autopilot`
- `codex/multiagente`

## Milestones

- Milestones por release, lidas de `docs/RELEASE_STATUS.yaml`.

## Sincronizacao

O script local `scripts/github-project-sync.ps1` cria:

- labels padrao;
- milestones por release;
- issues por task;
- Project v2 `AtendePro Roadmap`;
- inclusao das issues no Project.

Por seguranca, o script roda em modo simulacao por padrao. Use `-Apply` para criar no GitHub.

O script local `scripts/github-release-finalize.ps1` finaliza uma release ja marcada como `CONCLUIDA` em `docs/RELEASE_STATUS.yaml`:

- cria milestone/issue ausente quando usado com `-EnsureIssues`;
- fecha issues com tasks `CONCLUIDA`;
- fecha milestone apenas quando nao houver issue aberta;
- roda em dry-run por padrao e aplica mudancas somente com `-Apply`.

## Pre-requisito

Autenticar GitHub CLI com escopos de repo e project:

```powershell
gh auth login --web --scopes "repo,project"
```

## Simular

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\github-project-sync.ps1
```

Simular finalizacao de release:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\github-release-finalize.ps1 -Release R27 -EnsureIssues
```

## Aplicar

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\github-project-sync.ps1 -Apply
```

Aplicar finalizacao de release:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\github-release-finalize.ps1 -Release R27 -EnsureIssues -Apply
```

## Conferir autenticacao

```powershell
gh auth status
```

## Como usar nas tasks

Ao iniciar uma task:

- mover a issue para `In Progress`;
- criar branch com prefixo `codex/`;
- referenciar a issue no commit ou PR.

Ao finalizar:

- mover para `Review` ou `Done`;
- associar PR/commit;
- manter a milestone da release;
- executar `github-release-finalize.ps1` depois do merge quando a release estiver concluida;
- registrar falhas no observability quando houver bloqueio.
