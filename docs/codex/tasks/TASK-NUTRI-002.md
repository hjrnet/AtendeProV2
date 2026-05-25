# TASK-NUTRI-002 — Criar módulo Nutri Pro operacional

## Release
R10 — Nutri Pro, Beauty Pro e comercial

## Complexidade
ALTA

## Objetivo
Criar a base operacional real do módulo Nutri Pro, com backend hexagonal, rotas web por feature e estrutura inicial para que a nutricionista trabalhe dentro da vertical.

## Contexto
R7 documentou e apresentou o Nutri Pro como vertical. A R10 deve transformar a vertical em módulo utilizável, começando por uma base técnica e UX própria, sem tentar entregar plano alimentar completo nesta primeira task.

## Escopo permitido
- Criar módulo backend `nutri` com arquitetura hexagonal.
- Criar status operacional do Nutri Pro por empresa/tenant.
- Criar endpoints iniciais para carregar visão operacional do Nutri Pro.
- Criar feature web `nutri-pro` com tela principal da vertical.
- Conectar a vertical ao shell existente sem navegação por scroll.
- Reaproveitar clientes/pacientes, agenda, documentos, precificação e carimbo profissional.
- Preparar contratos para prontuário, avaliações, planos e documentos das próximas tasks.

## Fora de escopo
- Não criar plano alimentar completo.
- Não criar refeições, alimentos, suplementos ou macros ainda.
- Não criar PDF do plano alimentar ainda.
- Não criar app mobile.
- Não usar tabela alimentar real.
- Não fazer push.

## Critérios de aceite
- Nutri Pro abre como área operacional real no `/app`.
- Há endpoint backend da visão Nutri Pro com dados por tenant.
- Controller não expõe persistência.
- Código segue Request, Response, Command, Result, UseCase, InputPort, OutputPort e Adapter.
- Tela web usa feature architecture e mobile-first.
- Build/testes passam.
- Docker/local validado.

## Validação
- `mvn test`
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- Docker/local.
- Login com `karol.nutri@atendepro.local`.
- Abrir Nutri Pro e confirmar que a área operacional carrega.

## Commit esperado ao executar

```bash
git commit -m "feat(nutri): criar modulo operacional Nutri Pro (TASK-NUTRI-002)"
```

## Prompt recomendado

```md
Execute TASK-NUTRI-002 — Criar módulo Nutri Pro operacional seguindo o Harness Profissional.
Implemente somente a base operacional da vertical, com backend hexagonal, tela web por feature, validações e commit local. Não implemente plano alimentar completo ainda.
```

## Execução

Status: CONCLUIDA.

Resumo:
- Criado módulo backend `nutri` com arquitetura hexagonal para visão operacional do Nutri Pro.
- Criado endpoint `GET /api/nutri-pro/visao`, tenant-scoped por `empresaId`, usando `Command`, `Result`, `UseCase`, `InputPort`, `OutputPort` e adapter JDBC.
- A visão operacional reaproveita núcleo comum: pacientes/clientes, agenda, serviços/procedimentos, documentos profissionais e precificação.
- Criada feature web `nutri-pro` com tela operacional dentro de Verticais > Nutri Pro no `/app`.
- Preparados atalhos para as próximas tasks sem implementar plano alimentar, avaliação, exames, PDF ou app mobile fora do escopo.

Validação:
- `mvn test` — passou com 274 testes.
- `corepack pnpm typecheck` — passou.
- `corepack pnpm lint` — passou.
- `corepack pnpm build` — passou.
- Docker/local — Postgres e Mailpit saudáveis; backend local reiniciado com Java 21; web local reiniciada em `127.0.0.1:3000`.
- API local — login `karol.nutri@atendepro.local` e `GET /api/nutri-pro/visao` retornaram status `OPERACIONAL`, 8 indicadores e 5 pacientes recentes.
- Browser — Verticais > Nutri Pro carregou área operacional sem erros de console.

Commit:
- `feat(nutri): criar modulo operacional Nutri Pro (TASK-NUTRI-002)`
