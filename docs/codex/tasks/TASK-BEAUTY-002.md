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
