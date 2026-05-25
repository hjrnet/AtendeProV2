# TASK-NUTRI-006 — Criar documentos, exames, prescrições e dashboard Nutri Pro

## Release
R10 — Nutri Pro, Beauty Pro e comercial

## Complexidade
ALTA

## Objetivo
Completar o primeiro ciclo operacional do Nutri Pro com solicitações de exames, prescrições, PDF/carimbo CRN e dashboard da vertical.

## Escopo permitido
- Criar solicitação de exames laboratoriais por paciente.
- Criar prescrição de suplementação/formulações.
- Reaproveitar documentos profissionais e carimbo profissional.
- Gerar PDF inicial de plano alimentar ou documento nutricional com dados do paciente, nutricionista, CRN, assinatura/carimbo e aviso de responsabilidade.
- Criar dashboard Nutri Pro com pacientes, planos, avaliações, exames e alertas.
- Aplicar regra do Plano Estudante com marca d'água quando cabível.

## Fora de escopo
- Não criar QR Code avançado além do que já existe no módulo de documentos.
- Não criar assinatura digital externa.
- Não criar app mobile.
- Não usar dados reais.
- Não fazer push.

## Critérios de aceite
- Solicitações e prescrições ficam no prontuário.
- PDF inicial é gerado sem expor entidades JPA.
- Dashboard Nutri Pro mostra indicadores reais.
- Plano Estudante mantém marca d'água acadêmica.
- Build/testes passam.

## Validação
- `mvn test`
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- Gerar PDF em ambiente local.
- Validar com usuário demo Nutri e usuário Estudante.

## Commit esperado ao executar

```bash
git commit -m "feat(nutri): criar documentos exames prescricoes e dashboard (TASK-NUTRI-006)"
```

## Execução

Status: CONCLUIDA em modo autopilot multiagente release R10.

Entregue:
- Tipos documentais nutricionais no módulo de documentos profissionais: `SOLICITACAO_EXAMES`, `PRESCRICAO` e `PLANO_ALIMENTAR`.
- Dashboard Nutri Pro com indicadores reais de avaliações, exames, prescrições, documentos e planos.
- Ação `Adicionar exames laboratoriais` funcional no prontuário.
- Ação `Adicionar prescrição` funcional no prontuário.
- Documento/PDF inicial do plano alimentar reaproveitando o módulo de documentos profissionais.
- PDF com carimbo CRN validado via endpoint de documentos profissionais.
- Fluxo de Plano Estudante validado com PDF e marca d'água acadêmica existente.

Validação executada:
- `mvn test` — 281 testes passaram.
- `corepack pnpm typecheck` — passou.
- `corepack pnpm lint` — passou.
- `corepack pnpm build` — passou.
- `docker compose up -d` / `docker compose ps` — Postgres e Mailpit saudáveis.
- Backend e web locais reiniciados em `8080` e `3000`.
- API local validou criação de solicitação de exames, prescrição, carimbo CRN e PDF.
- API local validou documento/PDF com usuário Plano Estudante.
- Browser em `/app` validou exames, prescrição e documento/PDF do plano sem erros de console.
