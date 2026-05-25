# Protocolo de Continuidade

## Antes de cada task

1. Identificar release atual.
2. Identificar próxima task oficial.
3. Validar que a task existe em `docs/TASKS.md`, `docs/RELEASE_STATUS.yaml` e `docs/codex/tasks/TASK-XXXX.md`.
4. Ler task específica.
5. Planejar.

## Durante

1. Executar somente a task.
2. Não implementar task futura.
3. Não fazer push.
4. Manter arquitetura.

## Depois

1. Rodar testes.
2. Subir Docker/local.
3. Revisar arquitetura.
4. Atualizar status.
5. Atualizar SESSION_STATE.md.
6. Commit local se passar.
7. Recomendar próxima task.

## Se falhar

Não commitar. Relatar erro, causa provável e correção sugerida.

## Autopilot por release

### Identificação da release

1. Se o comando informar a release, como R0, R1 ou R2, usar exatamente essa release.
2. Se o comando não informar a release, usar a release atual registrada em `docs/RELEASE_STATUS.yaml` ou `docs/codex/SESSION_STATE.md`.
3. Se houver divergência entre arquivos de status, sessão, backlog e task individual, parar e gerar diagnóstico de inconsistência.

### Fluxo por task da release

Para cada task pendente da release:

1. Identificar a task oficial no backlog.
2. Confirmar que ela existe em `docs/TASKS.md`, `docs/codex/tasks/TASK-XXXX.md` e `docs/RELEASE_STATUS.yaml`, quando aplicável.
3. Criar plano curto internamente.
4. Executar somente essa task.
5. Não implementar task futura.
6. Rodar testes obrigatórios.
7. Rodar Docker/local quando aplicável.
8. Validar backend, frontend e banco quando existirem.
9. Revisar arquitetura.
10. Atualizar status da task.
11. Atualizar `docs/codex/SESSION_STATE.md`.
12. Executar `git status` e confirmar arquivos alterados.
13. Fazer `git add` dos arquivos da task.
14. Fazer commit local com Conventional Commits incluindo a task.
15. Nunca fazer push.
16. Avançar para a próxima task pendente da mesma release.

### Limites

- `autopilot release`: executar no máximo 3 tasks da release atual.
- `autopilot release R1 até 5 tasks`: executar no máximo 5 tasks da R1.
- `autopilot release R1 até concluir`: executar até concluir a R1 ou encontrar erro/bloqueio.
- `autopilot release até falhar`: executar a release atual até erro, bloqueio ou conclusão.

### Parada obrigatória

Parar imediatamente se:

1. testes falharem;
2. Docker/local falhar e a causa não for trivial;
3. houver conflito Git;
4. houver dúvida de produto;
5. houver alteração fora do escopo;
6. a task exigir credenciais externas;
7. a task exigir decisão humana;
8. a task afetar segurança de forma sensível sem clareza;
9. a task não existir no backlog oficial;
10. houver divergência entre `docs/TASKS.md`, `docs/RELEASE_STATUS.yaml` e task individual;
11. a release for concluída.

### Relatório final

Ao encerrar o ciclo, informar:

1. Release executada.
2. Tasks executadas.
3. Tasks pendentes da release.
4. Commits locais criados.
5. Testes executados.
6. Docker/local.
7. Erros ou bloqueios.
8. Status da release: `RELEASE EM ANDAMENTO`, `RELEASE CONCLUÍDA`, `RELEASE BLOQUEADA` ou `RELEASE FALHOU`.
9. Próxima ação recomendada.
10. Push: informar sempre que não foi realizado.
