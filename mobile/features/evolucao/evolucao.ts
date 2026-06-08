export type NivelEvolucao = "bom" | "estavel" | "atenção";

export type AcompanhamentoPaciente = {
  id: string;
  nomePaciente: string;
  area: string;
  ultimoRegistro: string;
  risco: NivelEvolucao;
  resumo: string;
};

export const pacientesEmAcompanhamento: AcompanhamentoPaciente[] = [
  {
    id: "ev-01",
    nomePaciente: "Patrícia Souza",
    area: "Nutri Pro",
    ultimoRegistro: "12/05/2026 — 18:00",
    risco: "estavel",
    resumo: "Progresso positivo, aderência acima de 85%."
  },
  {
    id: "ev-02",
    nomePaciente: "Rafael Lima",
    area: "Beauty Pro",
    ultimoRegistro: "11/05/2026 — 14:30",
    risco: "atenção",
    resumo: "Solicitou revisão de protocolo por alergia leve."
  },
  {
    id: "ev-03",
    nomePaciente: "Larissa Mendes",
    area: "Fisio Pro",
    ultimoRegistro: "11/05/2026 — 07:40",
    risco: "bom",
    resumo: "Dor em redução com redução de edema e melhor amplitude."
  }
];

export const totalAtencao = pacientesEmAcompanhamento.filter((item) => item.risco === "atenção").length;
