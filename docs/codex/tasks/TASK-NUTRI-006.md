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
