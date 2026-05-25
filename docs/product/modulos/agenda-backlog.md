# Agenda — Backlog Futuro

## Task de Especificação

| Task | Nome | Objetivo | Status |
|---|---|---|---|
| TASK-AGD-001 | Estruturar módulo Agenda do AtendePro | Definir especificação funcional, regras, UX, entidades e backlog. | CONCLUIDA |

## Tasks Futuras Sugeridas

| Task | Nome | Objetivo | Dependências |
|---|---|---|---|
| TASK-AGD-002 | Criar domínio de Agendamento | Modelar agendamento, status, tipos e recursos. | TASK-AGD-001 |
| TASK-AGD-003 | Criar cadastro de agendamento | Criar endpoint/use case de agendamento. | TASK-AGD-002 |
| TASK-AGD-004 | Criar listagem por dia | Listar agenda por data com filtros básicos. | TASK-AGD-003 |
| TASK-AGD-005 | Criar edição e cancelamento | Editar, cancelar, confirmar, realizar e registrar falta. | TASK-AGD-003 |
| TASK-AGD-006 | Criar validação de conflito | Validar sobreposição de profissional e recurso. | TASK-AGD-003 |
| TASK-AGD-007 | Criar disponibilidade profissional | Configurar agenda do profissional e horários disponíveis. | TASK-AGD-006 |
| TASK-AGD-008 | Criar recursos de agenda | Cadastrar recursos gerais para uso em agenda. | TASK-AGD-002 |
| TASK-AGD-009 | Criar visão semanal | Implementar API/UI para semana. | TASK-AGD-004 |
| TASK-AGD-010 | Criar filtros | Filtrar por profissional, cliente, status, procedimento e recurso. | TASK-AGD-004 |
| TASK-AGD-011 | Criar agenda responsiva | Criar UX mobile, tablet e desktop. | TASK-AGD-004, TASK-AGD-009 |
| TASK-AGD-012 | Preparar integração futura Google Agenda | Desenhar portas e eventos sem integração real. | TASK-AGD-005 |

## Observações

- Google Agenda, WhatsApp, pagamentos e notificações ficam fora da especificação inicial.
- Agenda deve continuar desacoplada de Nutri, Beauty, Biomed, Fisio, Spaces e demais verticais.
- Tasks futuras devem ganhar arquivos individuais em `docs/codex/tasks/` antes de execução oficial.
