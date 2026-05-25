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

## Isolamento por tenant

A regra de isolamento por `tenant_id` fica na camada de aplicação por `TenantAccessService`, usando o contexto resolvido por request. Usuários sem contexto de tenant ou com perfis globais (`SUPER_ADMIN`, `SUPORTE`) podem executar operações globais; usuários restritos só podem acessar a própria empresa. Violações retornam `TENANT_ACESSO_NEGADO` com HTTP 403.

## Perfis e permissões

Perfis de acesso mapeiam permissões de negócio em `PermissaoAcesso`, com strings de authority publicadas no JWT e na resposta de login. A validação backend inicial acontece por `PermissaoAcessoService` quando existe contexto de tenant; chamadas sem contexto seguem permitidas temporariamente para bootstrap e validação local até a proteção de rotas web/backend avançar.

## Login web

O login web fica em `features/auth`, com página raiz fina, schema Zod e client dedicado para `/api/auth/login`. A sessão usa `sessionStorage` nesta fase para evitar persistência permanente em `localStorage`, com fallback para ambientes de teste que bloqueiam storage. O backend aplica CORS em `/api/**` a partir de `app.cors.allowed-origins` para suportar o desenvolvimento local web + API.
