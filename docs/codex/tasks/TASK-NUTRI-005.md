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
