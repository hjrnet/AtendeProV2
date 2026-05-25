# Agenda — Regras de Negócio

## Regras Gerais

- A agenda é multiempresa e todo agendamento pertence a um tenant.
- A agenda não deve depender de uma vertical profissional específica.
- Um agendamento pode ter cliente/paciente, profissional, procedimento/serviço e recurso.
- Sublocação pode usar recurso sem cliente/paciente final.
- Atendimento interno pode existir sem cliente/paciente final quando representar reunião, bloqueio operacional ou tarefa interna futura.
- Duração pode vir do procedimento/serviço, mas pode ser ajustada no agendamento.

## Conflitos de Horário

- Um profissional não pode ter dois agendamentos bloqueantes no mesmo horário.
- Uma sala/recurso não pode estar em dois agendamentos bloqueantes no mesmo horário.
- Conflitos devem considerar sobreposição de início/fim, não apenas horários iguais.
- Agendamentos `CANCELADO` não bloqueiam horário.
- Agendamentos `REALIZADO` ficam bloqueados no histórico.
- Agendamentos `AGENDADO`, `CONFIRMADO`, `REALIZADO` e `REMARCADO` devem ser tratados com cuidado na regra técnica final.
- A regra de `REMARCADO` deve preservar histórico sem manter o horário antigo como bloqueio ativo, salvo se o desenho técnico futuro usar registro separado de histórico.

## Status

### AGENDADO

Estado inicial padrão quando o atendimento é criado e ainda não foi confirmado.

### CONFIRMADO

Paciente/cliente ou equipe confirmou presença.

### REALIZADO

Atendimento aconteceu e deve ficar bloqueado para histórico.

### CANCELADO

Atendimento não acontecerá e não deve bloquear horário.

### FALTOU

Paciente/cliente não compareceu. Deve ficar no histórico e pode alimentar relatórios.

### REMARCADO

Atendimento teve data/horário alterados. Deve manter histórico da alteração.

## Tipos de Atendimento

- `PRESENCIAL`: atendimento em local físico.
- `ONLINE`: atendimento remoto.
- `DOMICILIAR`: atendimento realizado fora da clínica, com tempo de deslocamento futuro.
- `SUBLOCACAO`: uso de recurso/espaço por parceiro ou profissional.
- `INTERNO`: agenda interna da empresa, sem obrigação de cliente/paciente.

## Recursos

- Recurso é opcional, mas quando informado deve respeitar conflito.
- Recursos podem representar sala, cadeira, cabine, maca, equipamento, consultório ou outro item operacional.
- Recurso pode ser reutilizado por Spaces/sublocação quando houver integração com esse módulo.
- Um recurso inativo não deve ser selecionado em novos agendamentos.

## Reagendamento

- Reagendamento deve manter histórico da data/hora anterior e nova.
- Reagendamento deve exigir motivo quando a regra comercial futura definir.
- Reagendamento deve revalidar conflito de profissional e recurso no novo horário.
- O histórico não deve apagar o agendamento original.

## Cancelamento

- Cancelamento deve registrar motivo em fase futura.
- Cancelamento não deve remover o registro.
- Cancelamento deve liberar horário para novo agendamento.

## Disponibilidade Profissional

- A agenda deve ser preparada para disponibilidade profissional futura.
- Disponibilidade poderá considerar dias da semana, horários, intervalos, férias, bloqueios e tempo de deslocamento.
- Disponibilidade não faz parte da implementação documental desta task.

## Dados Sensíveis

- Informações de cliente/paciente são sensíveis.
- Listagens e filtros devem respeitar tenant e permissões.
- Exportações futuras devem evitar vazamento entre empresas.
