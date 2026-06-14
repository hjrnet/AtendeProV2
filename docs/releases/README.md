# Releases AtendePro

Esta pasta guarda o resumo executivo de cada release concluida.

Regra operacional: toda release concluida deve ter um arquivo `Rxx.md` nesta pasta, alem de aparecer em `docs/RELEASE_STATUS.yaml`, `docs/TASKS.md`, `docs/ROADMAP_RELEASES.md` e, quando aplicavel, em `docs/codex/tasks/`.

Se uma release aparece nos documentos agregados, mas nao tem `docs/releases/Rxx.md`, isso deve ser tratado como divida de documentacao, nao como prova de que a release nao foi entregue.

Status atual documentado: R0 ate R32.

Leitura recomendada:

- `docs/releases/Rxx.md`: resumo executivo da release.
- `docs/TASKS.md`: backlog e tasks por release.
- `docs/RELEASE_STATUS.yaml`: fonte rapida de status maquina/humano.
- `docs/ROADMAP_RELEASES.md`: narrativa de roadmap e proximas etapas.
- `docs/codex/SESSION_STATE.md`: historico cronologico de execucao.

## Auditoria obrigatoria de documentacao

Ao finalizar uma release, a auditoria deve verificar entrega e documentacao. A entrega mostra que o produto evoluiu; a documentacao mostra que a evolucao ficou rastreavel para decisao, apresentacao e continuidade por outros agentes.

Comando padrao:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\release-documentation-audit.ps1
```

Regra minima:

- toda release em `docs/RELEASE_STATUS.yaml` deve ter `docs/releases/Rxx.md`;
- cada `Rxx.md` deve informar status, objetivo, tasks concluidas, evidencias e proxima release recomendada;
- ausencia de arquivo executivo deve bloquear fechamento da release;
- ausencia de secao recomendada deve gerar aviso, ou erro quando executado com `-Strict`.
