export type StatusCompromisso = "confirmado" | "agendado" | "concluido" | "cancelado";

export type AgendamentoPaciente = {
  id: string;
  horario: string;
  data: string;
  profissional: string;
  procedimento: string;
  local: string;
  status: StatusCompromisso;
};

export const agendamentosHoje: AgendamentoPaciente[] = [
  {
    id: "ag-01",
    horario: "08:30",
    data: "Hoje",
    profissional: "Dra. Carla Ribeiro",
    procedimento: "Consulta inicial",
    local: "Sala 2 — Clínica Central",
    status: "confirmado"
  },
  {
    id: "ag-02",
    horario: "14:00",
    data: "Hoje",
    profissional: "Enf. Mariana Luz",
    procedimento: "Acompanhamento de rotina",
    local: "Consultório 1",
    status: "agendado"
  },
  {
    id: "ag-03",
    horario: "17:20",
    data: "Hoje",
    profissional: "Nutricionista Karol",
    procedimento: "Reavaliação semanal",
    local: "Sala Virtual",
    status: "agendado"
  }
];

export const agendamentosFuturos: AgendamentoPaciente[] = [
  {
    id: "ag-04",
    horario: "08:30",
    data: "Seg 21",
    profissional: "Dra. Carla Ribeiro",
    procedimento: "Sessão de retorno",
    local: "Sala 1 — Clínica Central",
    status: "agendado"
  },
  {
    id: "ag-05",
    horario: "10:00",
    data: "Sex 21",
    profissional: "Nutricionista Karol",
    procedimento: "Ajuste de plano alimentar",
    local: "Sala Virtual",
    status: "agendado"
  },
  {
    id: "ag-06",
    horario: "16:00",
    data: "Dom 22",
    profissional: "Psic. Leandro",
    procedimento: "Consulta de retorno",
    local: "Sala 3",
    status: "agendado"
  }
];

export const horariosPrioritariosDoDia = [
  "09:00 check-in remoto",
  "09:30 revisão de metas",
  "10:30 retorno de prontuário"
];

export const totalAgendado = agendamentosHoje.length + agendamentosFuturos.length;
