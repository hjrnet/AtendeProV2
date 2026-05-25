# TASK-BEAUTY-001 — Criar módulo Beauty Pro operacional

## Release
R10 — Nutri Pro, Beauty Pro e comercial

## Complexidade
ALTA

## Objetivo
Criar a base operacional real do módulo Beauty Pro para estética, beleza e salões, com backend hexagonal e tela web própria.

## Contexto
R7 apresentou Beauty Pro no catálogo de verticais. A R10 deve transformar Beauty Pro em área utilizável, aproveitando clientes, agenda, serviços, estoque, equipamentos e precificação.

## Escopo permitido
- Criar módulo backend `beauty` com arquitetura hexagonal.
- Criar status operacional Beauty Pro por empresa/tenant.
- Criar endpoint de visão operacional da vertical.
- Criar feature web `beauty-pro`.
- Abrir Beauty Pro como área operacional no shell.
- Preparar contratos para ficha estética, protocolos, sessões, termos e dashboard.

## Fora de escopo
- Não criar protocolos completos ainda.
- Não criar upload real de fotos.
- Não criar prontuário estético profundo ainda.
- Não fazer push.

## Critérios de aceite
- Beauty Pro abre como área operacional real no `/app`.
- Backend respeita tenant e arquitetura hexagonal.
- Tela web é mobile-first e premium.
- Build/testes passam.

## Validação
- `mvn test`
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- Login com `ana.estetica@atendepro.local`.

## Commit esperado ao executar

```bash
git commit -m "feat(beauty): criar modulo operacional Beauty Pro (TASK-BEAUTY-001)"
```

## Execução registrada

- Status: CONCLUIDA.
- Backend: criado modulo `beauty` com command, use case, output port, service, adapter JDBC, controller e response para `/api/beauty-pro/visao`.
- Frontend: criada feature `beauty-pro` e integrada ao detalhe da vertical Beauty Pro no shell profissional.
- Validacao: `mvn test`, `corepack pnpm typecheck`, `corepack pnpm lint`, `corepack pnpm build`, backend/web locais e Browser com Ana Esteticista Demo.
- Proxima task: TASK-BEAUTY-002 — Criar ficha estetica, anamnese e avaliacao.
