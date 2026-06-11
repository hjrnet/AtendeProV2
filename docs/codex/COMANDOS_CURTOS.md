# Comandos Curtos — AtendePro

## status
Mostra release atual, última task, próxima task, estado do git, comandos disponíveis e riscos.

## planejar
Cria plano da próxima task e aguarda aprovação. Não altera arquivos.

## seguir
Executa plano aprovado.

## auto
Planeja internamente e executa uma task completa.

## economico
Executa uma task com economia de tokens. Relatório curto.

## multiagente
Executa uma task com revisão por papéis profissionais.

## autopilot 3 tasks
Executa até 3 tasks em sequência, uma por vez.

## autopilot economico 3 tasks
Executa até 3 tasks com economia de tokens.

## autopilot multiagente 3 tasks
Executa até 3 tasks com qualidade máxima e revisão por papéis.

## autopilot release
Executa até 3 tasks pendentes da release atual, uma por vez, até limite, erro, bloqueio ou conclusão.

## autopilot release atual
Executa tasks pendentes da release atual registrada em `docs/RELEASE_STATUS.yaml` ou `docs/codex/SESSION_STATE.md`.

## autopilot release R0
Executa tasks pendentes da release R0.

## autopilot release R1 até 5 tasks
Executa no máximo 5 tasks pendentes da R1.

## autopilot release R1 até concluir
Executa tasks pendentes da R1 até concluir a release ou encontrar erro/bloqueio.

## autopilot release até falhar
Executa a release atual até erro, bloqueio ou conclusão.

## executar release / concluir release / autopilot da release
Aliases para autopilot por release. Se a release não for informada, usar a release atual.

## observabilidade
Registrar eventos, falhas, relatorios e checklist automatico de release com `scripts/codex-observability.ps1`.

Exemplos:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\codex-observability.ps1 record -Release R18 -Task TASK-NUTRI-015 -Status INICIADA -Area backend -Summary "Inicio da task"
powershell -ExecutionPolicy Bypass -File .\scripts\codex-observability.ps1 fail -Release R18 -Task TASK-NUTRI-015 -Severity HIGH -FailureType test -Cause "Teste falhou" -Impact "Task bloqueada" -Action "Corrigir e repetir validacao"
powershell -ExecutionPolicy Bypass -File .\scripts\codex-observability.ps1 checklist -Release R18
powershell -ExecutionPolicy Bypass -File .\scripts\codex-observability.ps1 report -Release R18
```
