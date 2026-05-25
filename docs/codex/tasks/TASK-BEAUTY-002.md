# TASK-BEAUTY-002 — Criar ficha estética, anamnese e avaliação

## Release
R10 — Nutri Pro, Beauty Pro e comercial

## Complexidade
ALTA

## Objetivo
Criar ficha estética operacional do cliente, com anamnese, objetivos, contraindicações e avaliação inicial.

## Escopo permitido
- Criar perfil estético tenant-scoped por cliente.
- Registrar anamnese estética.
- Registrar objetivos: acne, manchas, rejuvenescimento, corporal, relaxamento, capilar, cílios/sobrancelhas ou salão.
- Registrar contraindicações e alertas.
- Criar histórico de avaliações.
- Criar tela responsiva no Beauty Pro.

## Fora de escopo
- Não criar diagnóstico médico.
- Não criar fotos reais.
- Não automatizar indicação de tratamento.
- Não fazer push.

## Critérios de aceite
- Profissional consegue abrir cliente no Beauty Pro.
- Ficha estética pode ser criada, editada e consultada.
- Contraindicações aparecem como alerta textual, não apenas cor.
- Build/testes passam.

## Validação
- `mvn test`
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- Browser com cliente demo Beauty.

## Commit esperado ao executar

```bash
git commit -m "feat(beauty): criar ficha estetica e anamnese (TASK-BEAUTY-002)"
```

## Execução registrada

- Status: CONCLUIDA.
- Backend: criada tabela `beauty_fichas_esteticas`, dominio de ficha estetica, objetivo estetico, commands/results, UseCases/InputPorts, OutputPorts, adapter JDBC e endpoints tenant-scoped.
- Frontend: Beauty Pro ganhou lista/busca de clientes, prontuario estetico, formulario de ficha/anamnese, alerta textual de contraindicações e historico de avaliacoes.
- Validacao: `mvn test`, `corepack pnpm typecheck`, `corepack pnpm lint`, `corepack pnpm build`, `docker compose ps`, API local com Ana demo e Browser com cliente demo Beauty.
- Proxima task: TASK-BEAUTY-003 — Criar protocolos, sessoes e evolucao Beauty Pro.
