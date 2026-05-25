# TASK-AGD-001 — Estruturar módulo Agenda do AtendePro

## Release
R7 — Verticais profissionais

## Tipo
Documentação de produto e especificação funcional.

## Objetivo
Definir a especificação funcional, regras de negócio, UX, entidades e futuras tasks do módulo Agenda do AtendePro.

## Contexto
A agenda será um módulo central e comum do AtendePro, usado por nutrição, estética, biomedicina, fisioterapia, salões, terapias, espaços compartilhados e atendimento domiciliar. Ela não deve ser acoplada a uma profissão específica.

A R3 já criou uma agenda base operacional. Esta task documental estrutura a evolução profissional do módulo para sustentar as próximas verticais e futuras melhorias do núcleo comum, sem reabrir implementação técnica nesta etapa.

## Escopo Permitido
- Documentar visão do módulo Agenda.
- Definir entidades/conceitos de domínio.
- Definir status de agendamento.
- Definir tipos de atendimento.
- Definir recursos de agenda.
- Definir regras de conflito.
- Definir visões dia, semana e lista.
- Definir UX mobile, tablet e desktop.
- Criar backlog futuro de implementação da agenda.
- Atualizar Harness Profissional.

## Fora de Escopo
- Não implementar backend.
- Não implementar frontend.
- Não criar integração Google Agenda.
- Não criar WhatsApp.
- Não criar pagamento.
- Não fazer push.

## Documentos Esperados
- `docs/product/modulos/agenda.md`
- `docs/product/modulos/agenda-regras-negocio.md`
- `docs/product/modulos/agenda-fluxos.md`
- `docs/product/modulos/agenda-telas.md`
- `docs/product/modulos/agenda-backlog.md`
- `docs/TASKS.md`
- `docs/ROADMAP_RELEASES.md`
- `docs/RELEASE_STATUS.yaml`

## Critérios de Aceite
- Visão do módulo Agenda documentada.
- Entidades e conceitos de domínio documentados.
- Status, tipos de atendimento e recursos documentados.
- Regras de conflito documentadas.
- Visões dia, semana e lista documentadas.
- UX mobile, tablet e desktop documentada.
- Backlog futuro de agenda criado.
- Nenhuma implementação técnica feita.
- Commit local criado.
- Push não realizado.

## Validação
- Verificar existência dos documentos esperados.
- Confirmar registro da task em `docs/TASKS.md`.
- Confirmar atualização de `docs/ROADMAP_RELEASES.md`.
- Confirmar atualização de `docs/RELEASE_STATUS.yaml`.
- Conferir que não houve alteração em backend, frontend ou banco.

## Prompt Recomendado

```md
Execute TASK-AGD-001 — Estruturar módulo Agenda do AtendePro seguindo o Harness Profissional.
Crie somente documentação oficial e atualize roadmap/backlog/status. Não implemente backend, frontend, integrações ou banco. Faça commit local e não faça push.
```
