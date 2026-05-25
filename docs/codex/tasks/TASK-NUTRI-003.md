# TASK-NUTRI-003 — Criar prontuário nutricional e menu rápido funcional

## Release
R10 — Nutri Pro, Beauty Pro e comercial

## Complexidade
ALTA

## Objetivo
Criar o prontuário nutricional do paciente e transformar o Menu Rápido Nutri Pro em ações funcionais.

## Escopo permitido
- Criar perfil nutricional tenant-scoped por paciente.
- Exibir prontuário nutricional com dados pessoais, status, resumo, anamnese inicial e atalhos.
- Implementar ações rápidas para avaliação antropométrica, gasto energético, exames laboratoriais e plano alimentar como entradas reais ou estados preparados.
- Priorizar ações usadas pela Karol: gastos energéticos, exames laboratoriais e plano alimentar.
- Criar lista/seleção de pacientes dentro do Nutri Pro.
- Criar estados vazio, carregando e erro.

## Fora de escopo
- Não calcular TMB/GET completo ainda.
- Não criar plano alimentar completo ainda.
- Não gerar PDF.
- Não criar app mobile.
- Não fazer push.

## Critérios de aceite
- Nutricionista consegue abrir paciente no Nutri Pro.
- Prontuário nutricional mostra central do paciente.
- Menu rápido tem ações reais ou estados de fluxo preparados, não apenas preview estático.
- Ações indisponíveis aparecem como próximas etapas, sem quebrar fluxo.
- Build/testes passam.

## Validação
- `mvn test`
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- Browser em desktop/mobile.
- Login demo Nutri e abertura de paciente.

## Commit esperado ao executar

```bash
git commit -m "feat(nutri): criar prontuario nutricional e menu rapido funcional (TASK-NUTRI-003)"
```

## Execução

- Status: CONCLUIDA
- Data: 2026-05-25
- Implementação:
  - Backend Nutri Pro passou a listar pacientes e consultar prontuário nutricional por paciente, com escopo por empresa/tenant.
  - API adicionada em `GET /api/nutri-pro/pacientes` e `GET /api/nutri-pro/pacientes/{pacienteId}/prontuario`.
  - Web Nutri Pro passou a exibir busca/lista de pacientes, central do paciente e menu rápido funcional.
  - Ações rápidas priorizadas: gasto energético, exames laboratoriais e plano alimentar.
  - Ações indisponíveis permanecem como estados preparados para as próximas tasks da R10.
- Validação:
  - `mvn test` — PASSOU, 276 testes.
  - `corepack pnpm typecheck` — PASSOU.
  - `corepack pnpm lint` — PASSOU.
  - `corepack pnpm build` — PASSOU.
  - `docker compose ps` — PostgreSQL e Mailpit saudáveis.
  - API local com login `karol.nutri@atendepro.local` — PASSOU.
  - Browser em `/app`, Verticais > Nutri Pro — PASSOU, sem erros de console.
- Fora de escopo respeitado:
  - Não foi criado cálculo completo de TMB/GET.
  - Não foi criado plano alimentar completo.
  - Não foi gerado PDF.
  - Não foi criado app mobile.
  - Push não foi realizado.
