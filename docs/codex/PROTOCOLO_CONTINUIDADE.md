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
