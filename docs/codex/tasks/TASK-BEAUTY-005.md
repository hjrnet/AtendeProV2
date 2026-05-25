# TASK-BEAUTY-005 — Integrar Beauty Pro com agenda, precificação e experiência web

## Release
R10 — Nutri Pro, Beauty Pro e comercial

## Complexidade
ALTA

## Objetivo
Finalizar o primeiro ciclo operacional do Beauty Pro integrando agenda, serviços/procedimentos, custos, precificação e experiência web.

## Escopo permitido
- Integrar protocolos e sessões à agenda base.
- Exibir serviços/procedimentos Beauty dentro da vertical.
- Exibir simulações de preço relevantes ao Beauty Pro.
- Destacar procedimentos com margem baixa ou prejuízo.
- Criar fluxo web de trabalho: cliente → ficha → protocolo → sessão → evolução → precificação.
- Melhorar responsividade mobile/tablet/desktop.

## Fora de escopo
- Não criar pagamento real.
- Não criar WhatsApp.
- Não criar app Expo.
- Não criar IA.
- Não fazer push.

## Critérios de aceite
- Beauty Pro tem fluxo operacional navegável.
- Agenda e precificação aparecem integradas à vertical.
- Lista de clientes/protocolos tem busca, filtro e estado vazio.
- Mobile mostra uma seção por vez.
- Build/testes passam.

## Validação
- `mvn test`
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- Browser em mobile/tablet/desktop.
- Login com `ana.estetica@atendepro.local`.

## Commit esperado ao executar

```bash
git commit -m "feat(beauty): integrar agenda precificacao e experiencia web (TASK-BEAUTY-005)"
```
