# Agenda — Telas e UX

## Princípios de UX

- Produto SaaS moderno, limpo e premium.
- Foco em velocidade operacional, clareza e baixa fricção.
- Agenda deve servir profissionais de saúde, beleza, terapias, spaces e atendimento domiciliar.
- Toda lista deve ter busca/filtro.
- Interface deve funcionar bem em mobile, tablet e desktop.

## Visão Mobile

Mobile deve priorizar visão de dia em lista.

Requisitos:

- cards grandes por agendamento;
- botões principais fáceis de tocar;
- navegação simples entre dias;
- filtros compactos;
- ação rápida para criar agendamento;
- status visual claro;
- detalhes em tela/modal simples.

## Visão Tablet

Tablet deve permitir agenda em duas colunas ou semana compacta.

Requisitos:

- lista do dia ao lado dos detalhes;
- semana compacta com cards menores;
- filtros acessíveis sem ocupar a tela toda;
- edição rápida de status;
- bom uso em recepção ou atendimento.

## Visão Desktop

Desktop deve priorizar visão semanal, filtros laterais e cards por horário.

Requisitos:

- navegação por dia/semana;
- grade semanal;
- filtros por profissional, cliente, status, procedimento e recurso;
- cards por horário;
- criação rápida em horário selecionado;
- lista alternativa para operações densas;
- estados vazio, carregando e erro.

## Telas Previstas

### Agenda Dia

Lista cronológica dos agendamentos do dia, com filtros, busca e ações de status.

### Agenda Semana

Grade semanal com colunas por dia ou por profissional, adequada para desktop/tablet.

### Agenda Lista

Lista paginada/rolável com busca, filtros e ordenação.

### Criar Agendamento

Formulário com cliente/paciente, profissional, procedimento/serviço, recurso, data, horário, duração, tipo de atendimento e observações.

### Detalhe do Agendamento

Resumo completo com status, dados de cliente, profissional, serviço, recurso, horário, duração, tipo, observações e histórico futuro.

### Reagendar

Tela/modal para escolher nova data e horário com validação de conflito.

### Cancelar

Tela/modal com confirmação e motivo futuro.

### Filtros

Painel de filtros por profissional, cliente, status, procedimento e recurso.

### Disponibilidade Profissional Futura

Tela futura para horários de atendimento, intervalos, bloqueios, férias e exceções.

## Componentes Esperados

- seletor de data;
- seletor de visão;
- filtros;
- cards de agendamento;
- chips de status;
- modal/form de agendamento;
- ações rápidas de confirmar, realizar, cancelar e falta;
- estado vazio;
- indicador de conflito.
