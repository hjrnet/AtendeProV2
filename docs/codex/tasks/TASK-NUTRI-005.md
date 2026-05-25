# TASK-NUTRI-005 — Criar plano alimentar com refeições, alimentos e suplementos

## Release
R10 — Nutri Pro, Beauty Pro e comercial

## Complexidade
ALTA

## Objetivo
Criar o fluxo operacional inicial de plano alimentar por paciente, com refeições, alimentos personalizados, suplementos/formulações e cálculo inicial de energia/macros.

## Escopo permitido
- Criar plano alimentar por paciente.
- Criar refeições com nome, horário, observações e ordenação.
- Criar banco inicial de alimentos personalizados por empresa.
- Criar banco inicial de suplementos/formulações por empresa.
- Adicionar alimentos e suplementos às refeições.
- Calcular energia, proteínas, carboidratos e lipídios de forma proporcional.
- Exibir resumo diário e por refeição.
- Permitir status: rascunho, ativo, substituído e arquivado.

## Fora de escopo
- Não importar tabela alimentar real.
- Não criar assistente inteligente.
- Não criar app mobile.
- Não gerar PDF final ainda.
- Não fazer push.

## Critérios de aceite
- Nutricionista consegue criar plano alimentar para paciente.
- Nutricionista consegue criar refeições e adicionar itens.
- Sistema calcula totais básicos de energia e macros.
- Plano ativo aparece no prontuário.
- Build/testes passam.

## Validação
- `mvn test`
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- Browser com criação de plano demo.

## Commit esperado ao executar

```bash
git commit -m "feat(nutri): criar plano alimentar com refeicoes e macros (TASK-NUTRI-005)"
```

## Execução

Status: CONCLUIDA em modo autopilot multiagente release R10.

Entregue:
- Backend hexagonal para criar, listar e detalhar planos alimentares por paciente.
- Tabelas tenant-scoped para planos, refeições, itens, alimentos personalizados e suplementos/formulações.
- Cálculo proporcional de energia, proteínas, carboidratos e lipídios por item, refeição e plano.
- Ação rápida `Adicionar plano alimentar` funcional no prontuário Nutri Pro.
- UI responsiva com criação de plano inicial, resumo diário, refeições, itens e histórico.

Validação executada:
- `mvn test` — 281 testes passaram.
- `corepack pnpm typecheck` — passou.
- `corepack pnpm lint` — passou.
- `corepack pnpm build` — passou.
- `docker compose up -d` / `docker compose ps` — Postgres e Mailpit saudáveis.
- Backend local reiniciado com profile `local` — Liquibase com 31 changesets registrados.
- API local validou login da Karol, criação, listagem e detalhe de plano alimentar.
- Browser em `/app` validou Verticais > Nutri Pro > Adicionar plano alimentar sem erros de console.
