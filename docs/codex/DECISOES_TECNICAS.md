# Decisões Técnicas

## Backend
Spring Boot + Java 21 + Hexagonal Architecture.

## Frontend
Next.js + React + TypeScript + Tailwind + shadcn/ui.

## Mobile
Expo + React Native.

## Banco
PostgreSQL + Liquibase.

## Estratégia
SaaS modular multiárea, núcleo comum e verticais.

## IA
Codex/Antigravity trabalha por Harness Profissional com commands: status, planejar, seguir, auto, economico, multiagente, autopilot.

## Autopilot por release

O Harness Profissional aceita comandos de autopilot por release, incluindo:

- `autopilot release`
- `autopilot release atual`
- `autopilot da release`
- `executar release`
- `concluir release`
- `autopilot R0`
- `autopilot R1`
- `autopilot release R2`
- `autopilot release R1 até 5 tasks`
- `autopilot release R1 até concluir`
- `autopilot release até falhar`

Se a release for informada, usar exatamente a release solicitada. Se não for informada, usar a release atual de `docs/RELEASE_STATUS.yaml` ou `docs/codex/SESSION_STATE.md`. Divergência entre arquivos oficiais bloqueia a execução e exige diagnóstico.

O limite padrão de `autopilot release` é de 3 tasks da release atual. Cada task concluída deve gerar commit local individual com Conventional Commits e referência da task. Push permanece manual.
