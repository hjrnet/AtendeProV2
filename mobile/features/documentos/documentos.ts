export type TipoDocumento = "avaliação" | "prescrição" | "exame" | "parecer";

export type DocumentoPaciente = {
  id: string;
  titulo: string;
  tipo: TipoDocumento;
  origem: string;
  data: string;
  descricao: string;
  assinado: boolean;
};

export const documentosPendentes: DocumentoPaciente[] = [
  {
    id: "doc-01",
    titulo: "Plano alimentar inicial",
    tipo: "prescrição",
    origem: "Nutricionista Karol",
    data: "12/05/2026",
    descricao: "Versão revisada com metas de hidratação e rotina de sono.",
    assinado: true
  },
  {
    id: "doc-02",
    titulo: "Relatório de evolução",
    tipo: "avaliação",
    origem: "Dr. Paulo Mendes",
    data: "09/05/2026",
    descricao: "Sintomas estáveis, sem novos eventos adversos.",
    assinado: false
  },
  {
    id: "doc-03",
    titulo: "Resultado de hemograma",
    tipo: "exame",
    origem: "Laboratório CentroSul",
    data: "02/05/2026",
    descricao: "Valores dentro dos parâmetros, sem urgência clínica.",
    assinado: false
  }
];

export const documentosAntigos: DocumentoPaciente[] = [
  {
    id: "doc-04",
    titulo: "Termo de consentimento",
    tipo: "parecer",
    origem: "Clínica Central",
    data: "28/04/2026",
    descricao: "Documento de consentimento geral para atendimento clínico.",
    assinado: true
  }
];

export const fotosEvidencia = [
  { id: "ft-01", nome: "Progresso inicial", data: "01/05/2026", etapa: "Foto 01" },
  { id: "ft-02", nome: "Progresso intermediário", data: "08/05/2026", etapa: "Foto 02" },
  { id: "ft-03", nome: "Retorno de rotina", data: "12/05/2026", etapa: "Foto 03" }
];

export const totalDocumentos = documentosPendentes.length + documentosAntigos.length;
