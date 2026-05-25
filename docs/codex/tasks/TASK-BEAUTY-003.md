# TASK-BEAUTY-003 — Criar protocolos, sessões e evolução Beauty Pro

## Release
R10 — Nutri Pro, Beauty Pro e comercial

## Complexidade
ALTA

## Objetivo
Criar protocolos estéticos e de beleza, pacotes de sessões, execução de sessão e evolução do cliente.

## Escopo permitido
- Criar protocolo por cliente.
- Registrar tipo de protocolo: facial, corporal, capilar, cílios/sobrancelhas, salão ou personalizado.
- Vincular serviços/procedimentos existentes.
- Criar pacote de sessões com quantidade prevista, realizadas e status.
- Registrar evolução por sessão.
- Integrar com agenda quando houver compromisso relacionado.

## Fora de escopo
- Não criar prontuário médico.
- Não criar upload real de fotos.
- Não criar recomendação automática.
- Não fazer push.

## Critérios de aceite
- Profissional consegue criar protocolo e sessões.
- Sessões ficam no histórico do cliente.
- Status do pacote é claro: ativo, concluído, cancelado ou pausado.
- Build/testes passam.

## Validação
- `mvn test`
- `corepack pnpm typecheck`
- `corepack pnpm lint`
- `corepack pnpm build`
- Teste manual com cliente demo Beauty.

## Commit esperado ao executar

```bash
git commit -m "feat(beauty): criar protocolos sessoes e evolucao (TASK-BEAUTY-003)"
```

## Execução registrada

- Status: CONCLUIDA.
- Backend: criadas tabelas `beauty_protocolos` e `beauty_sessoes_protocolos`, dominio de protocolo/pacote/sessao, commands/results, UseCases/InputPorts, OutputPorts, adapter JDBC e endpoints tenant-scoped.
- Frontend: Beauty Pro ganhou painel de protocolos com criacao de pacote, lista de status, historico de sessoes e registro de execucao/evolucao.
- Validacao: `mvn test`, `corepack pnpm typecheck`, `corepack pnpm lint`, `corepack pnpm build`, `docker compose ps`, API local com Ana demo e Browser com cliente demo Beauty.
- Proxima task: TASK-BEAUTY-004 — Criar termos, fotos placeholder, produtos/lotes e dashboard Beauty Pro.
