# Agenda — Fluxos Principais

## Fluxo 1 — Criar Agendamento

1. Abrir a agenda.
2. Selecionar visão de dia, semana ou lista.
3. Acionar novo agendamento.
4. Selecionar cliente/paciente quando aplicável.
5. Selecionar profissional.
6. Selecionar procedimento/serviço.
7. Selecionar recurso quando aplicável.
8. Definir tipo de atendimento.
9. Definir data e horário.
10. Definir duração, usando sugestão do procedimento quando houver.
11. Informar observações.
12. Validar conflitos de profissional e recurso.
13. Salvar como `AGENDADO`.

## Fluxo 2 — Confirmar Agendamento

1. Abrir agendamento.
2. Conferir dados principais.
3. Marcar como confirmado.
4. Registrar data/hora da confirmação em fase futura.
5. Atualizar status para `CONFIRMADO`.

## Fluxo 3 — Marcar como Realizado

1. Abrir agendamento confirmado ou agendado.
2. Marcar como realizado.
3. Preservar histórico.
4. Atualizar status para `REALIZADO`.
5. Liberar ações futuras de documento, evolução ou cobrança quando aplicável.

## Fluxo 4 — Registrar Falta

1. Abrir agendamento.
2. Marcar como faltou.
3. Registrar observação opcional.
4. Atualizar status para `FALTOU`.
5. Alimentar relatórios futuros de falta.

## Fluxo 5 — Cancelar Agendamento

1. Abrir agendamento.
2. Acionar cancelamento.
3. Informar motivo quando obrigatório.
4. Atualizar status para `CANCELADO`.
5. Liberar horário para novo agendamento.

## Fluxo 6 — Reagendar

1. Abrir agendamento.
2. Acionar reagendamento.
3. Selecionar nova data/horário.
4. Ajustar duração se necessário.
5. Validar conflito no novo horário.
6. Registrar histórico da alteração.
7. Atualizar status/registro conforme desenho técnico futuro.

## Fluxo 7 — Filtrar Agenda

1. Abrir visão de agenda.
2. Selecionar filtros por profissional, cliente, status, procedimento e recurso.
3. Aplicar busca textual quando disponível.
4. Navegar por dia, semana ou lista.
5. Limpar filtros.

## Fluxo 8 — Atendimento de Sublocação

1. Selecionar tipo `SUBLOCACAO`.
2. Selecionar recurso.
3. Selecionar profissional/parceiro quando aplicável.
4. Cliente/paciente final pode ficar vazio.
5. Definir período e duração.
6. Validar conflito do recurso.
7. Salvar agendamento.
