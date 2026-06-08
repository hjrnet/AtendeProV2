export type OrigemMensagem = "cliente" | "profissional" | "sistema";

export type MensagemDireta = {
  id: string;
  origem: OrigemMensagem;
  remetente: string;
  hora: string;
  texto: string;
  naoLida: boolean;
};

export const mensagensCliente: MensagemDireta[] = [
  {
    id: "msg-01",
    origem: "profissional",
    remetente: "Karol",
    hora: "09:18",
    texto: "Seu plano alimentar foi atualizado até domingo. Qualquer dúvida, me avise.",
    naoLida: true
  },
  {
    id: "msg-02",
    origem: "sistema",
    remetente: "AtendePro",
    hora: "08:40",
    texto: "Consulta confirmada para amanhã às 08:30 com preparação prévia.",
    naoLida: true
  },
  {
    id: "msg-03",
    origem: "profissional",
    remetente: "Dr. Paulo",
    hora: "Ontem",
    texto: "Seus resultados foram validados e não exigem retorno emergencial.",
    naoLida: false
  }
];

export const mensagensPendentesProfissional: MensagemDireta[] = [
  {
    id: "msgp-01",
    origem: "cliente",
    remetente: "Patrícia Souza",
    hora: "09:22",
    texto: "Posso remarcar para sábado?",
    naoLida: true
  },
  {
    id: "msgp-02",
    origem: "cliente",
    remetente: "Rafael Lima",
    hora: "08:58",
    texto: "Recebi a prescrição, está claro?",
    naoLida: true
  },
  {
    id: "msgp-03",
    origem: "cliente",
    remetente: "Larissa Mendes",
    hora: "Ontem",
    texto: "Obrigada, vou fazer o ajuste e te conto no retorno.",
    naoLida: false
  }
];

export const mensagensNaoLidas = mensagensCliente.filter((mensagem) => mensagem.naoLida).length;
