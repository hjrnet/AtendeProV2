# TASK-NUTRI-004 — Criar avaliação antropométrica e gasto energético

## Release
R10 — Nutri Pro, Beauty Pro e comercial

## Complexidade
ALTA

## Objetivo
Permitir que a nutricionista registre avaliação antropométrica e estime gasto energético de forma operacional.

## Escopo permitido
- Criar domínio de avaliação antropométrica.
- Registrar peso, altura, idade, sexo, IMC, objetivo e observações.
- Criar histórico por paciente.
- Criar estimativa inicial de TMB/GEB/GET com fórmulas documentadas e validação profissional.
- Exibir aviso de que cálculos são estimativos e não substituem decisão técnica.
- Criar tela responsiva no prontuário nutricional.

## Fora de escopo
- Não criar dobras cutâneas, bioimpedância avançada ou fotos.
- Não automatizar conduta clínica.
- Não criar PDF.
- Não fazer push.

## Critérios de aceite
- Avaliação pode ser criada, listada e detalhada por paciente.
- IMC é calculado corretamente.
- Gasto energético estimado aparece de forma clara.
- Histórico fica preservado.
- Build/testes passam.

## Validação
- `mvn test`
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- Teste manual com paciente demo.

## Commit esperado ao executar

```bash
git commit -m "feat(nutri): criar avaliacao antropometrica e gasto energetico (TASK-NUTRI-004)"
```
