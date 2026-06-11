# GitHub Projects — AtendePro Roadmap

Esta configuracao cria a visao web de acompanhamento curto prazo do AtendePro no GitHub Projects.

## Objetivo

- Transformar releases e `TASK-*` em uma visao web acompanhavel.
- Conectar backlog, commits, branches, issues, milestones e PRs.
- Manter o reposititorio como fonte tecnica e o GitHub Project como visao executiva.

## Project recomendado

- Nome: `AtendePro Roadmap`
- Repositorio: `hjrnet/AtendeProV2`
- Escopo inicial: `R18` e `R19`
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

- `release/R18`
- `release/R19`
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

- `R18 — Nutri Pro plano alimentar avançado e produtividade clínica`
- `R19 — Growth, inteligência e refinamento comercial das duas verticais`

## Sincronizacao

O script local `scripts/github-project-sync.ps1` cria:

- labels padrao;
- milestones por release;
- issues por task;
- Project v2 `AtendePro Roadmap`;
- inclusao das issues no Project.

Por seguranca, o script roda em modo simulacao por padrao. Use `-Apply` para criar no GitHub.

## Pre-requisito

Autenticar GitHub CLI com escopos de repo e project:

```powershell
gh auth login --web --scopes "repo,project"
```

## Simular

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\github-project-sync.ps1
```

## Aplicar

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\github-project-sync.ps1 -Apply
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
- registrar falhas no observability quando houver bloqueio.
