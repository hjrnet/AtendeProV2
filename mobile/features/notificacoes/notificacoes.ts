export type TipoNotificacao = "agenda" | "documento" | "sistema" | "resultado";

export type Notificacao = {
  id: string;
  titulo: string;
  descricao: string;
  categoria: TipoNotificacao;
  dataHora: string;
  lida: boolean;
  urgente: boolean;
};

export const notificacoes: Notificacao[] = [
  {
    id: "not-01",
    titulo: "Consulta confirmada",
    descricao: "Seu retorno foi confirmado para hoje às 14:00 com Enf. Mariana.",
    categoria: "agenda",
    dataHora: "Hoje, 07:10",
    lida: true,
    urgente: false
  },
  {
    id: "not-02",
    titulo: "Novo documento disponível",
    descricao: "Relatório de evolução da semana aguardando revisão.",
    categoria: "documento",
    dataHora: "Hoje, 09:31",
    lida: false,
    urgente: true
  },
  {
    id: "not-03",
    titulo: "Resultado de exame",
    descricao: "Hemograma carregado. Não houve sinais de alerta.",
    categoria: "resultado",
    dataHora: "Ontem",
    lida: false,
    urgente: false
  },
  {
    id: "not-04",
    titulo: "Lembrete de protocolo",
    descricao: "Lembre-se de registrar sessão de feedback até 21h.",
    categoria: "sistema",
    dataHora: "Ontem",
    lida: true,
    urgente: false
  }
];

export const totalNaoLidas = notificacoes.filter((item) => !item.lida).length;
