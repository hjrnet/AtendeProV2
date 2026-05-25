# TASK-BEAUTY-004 — Criar termos, fotos placeholder, produtos/lotes e dashboard Beauty Pro

## Release
R10 — Nutri Pro, Beauty Pro e comercial

## Complexidade
ALTA

## Objetivo
Criar documentos e controles de segurança do Beauty Pro, incluindo termos, evidências seguras sem fotos reais, produtos/lotes e dashboard da vertical.

## Escopo permitido
- Criar termos de consentimento vinculados ao cliente/protocolo.
- Reaproveitar módulo de documentos profissionais quando possível.
- Criar placeholders de fotos de evolução sem imagem real de pessoa.
- Vincular produtos/insumos/lotes do estoque a protocolos ou sessões.
- Criar alertas de validade e estoque baixo no contexto Beauty Pro.
- Criar dashboard Beauty Pro com clientes, protocolos, sessões, pacotes, alertas e margem.

## Fora de escopo
- Não implementar upload/storage real de imagens.
- Não usar fotos reais.
- Não criar assinatura externa.
- Não fazer push.

## Critérios de aceite
- Termos aparecem no histórico do cliente.
- Produtos/lotes podem ser vinculados a protocolos/sessões.
- Placeholder de evolução é seguro e não usa imagem real.
- Dashboard Beauty Pro mostra indicadores reais.
- Build/testes passam.

## Validação
- `mvn test`
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- Browser com dashboard Beauty Pro.

## Commit esperado ao executar

```bash
git commit -m "feat(beauty): criar termos produtos e dashboard (TASK-BEAUTY-004)"
```
