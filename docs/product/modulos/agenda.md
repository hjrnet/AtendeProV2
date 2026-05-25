# Agenda — Visão Oficial

## Propósito

A Agenda é um módulo central e comum do AtendePro. Ela deve atender nutrição, estética, biomedicina, fisioterapia, salões, terapias, espaços compartilhados e atendimento domiciliar sem ficar acoplada a uma profissão específica.

O módulo deve organizar compromissos, profissionais, clientes/pacientes, procedimentos/serviços e recursos de atendimento, preservando histórico operacional e prevenindo conflitos de horário.

## Posicionamento no Produto

A Agenda pertence ao núcleo comum do AtendePro e deve ser reutilizada pelas verticais profissionais. Ela também deve conversar com:

- clientes/pacientes;
- usuários/profissionais;
- serviços/procedimentos;
- recursos físicos;
- Spaces/sublocação;
- documentos;
- notificações futuras;
- app do paciente/profissional futuro;
- relatórios e dashboard.

## Objetivos do Módulo

- Criar, editar, cancelar e reagendar atendimentos.
- Permitir confirmação, realização e registro de falta.
- Prevenir conflito de profissional e recurso.
- Oferecer visões por dia, semana e lista.
- Funcionar bem em mobile, tablet e desktop.
- Preparar base para disponibilidade profissional.
- Preparar integrações futuras sem acoplar a agenda a Google, WhatsApp, pagamento ou qualquer vertical.

## Entidades e Conceitos de Domínio

- Agendamento.
- Cliente/paciente do agendamento.
- Profissional responsável.
- Procedimento/serviço.
- Recurso de agenda.
- Tipo de atendimento.
- Status de agendamento.
- Janela de horário.
- Duração.
- Histórico de reagendamento.
- Motivo de cancelamento.
- Disponibilidade profissional futura.
- Bloqueio de agenda futuro.
- Observação interna.
- Observação visível ao paciente futura.

## Status de Agendamento

- `AGENDADO`
- `CONFIRMADO`
- `REALIZADO`
- `CANCELADO`
- `FALTOU`
- `REMARCADO`

## Tipos de Atendimento

- `PRESENCIAL`
- `ONLINE`
- `DOMICILIAR`
- `SUBLOCACAO`
- `INTERNO`

## Recursos de Agenda

- `SALA`
- `CADEIRA`
- `CABINE`
- `MACA`
- `EQUIPAMENTO`
- `CONSULTORIO`
- `OUTRO`

## Fora de Escopo Desta Especificação

- Implementação backend.
- Implementação frontend.
- Integração Google Agenda.
- WhatsApp.
- Pagamento.
- Push automático.

## Documentos Relacionados

- `docs/product/modulos/agenda-regras-negocio.md`
- `docs/product/modulos/agenda-fluxos.md`
- `docs/product/modulos/agenda-telas.md`
- `docs/product/modulos/agenda-backlog.md`
