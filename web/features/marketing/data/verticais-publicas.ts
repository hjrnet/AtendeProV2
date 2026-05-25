export type VerticalPublica = {
  slug: string;
  nome: string;
  categoria: string;
  conselho: string;
  resumo: string;
  destaque: string;
  cenario: string;
  capacidades: string[];
  documentos: string[];
  jornada: string[];
  metricas: Array<{
    rotulo: string;
    valor: string;
  }>;
};

export const verticaisPublicas: VerticalPublica[] = [
  {
    slug: "nutri-pro",
    nome: "Nutri Pro",
    categoria: "Nutrição",
    conselho: "CRN",
    resumo:
      "Prontuário nutricional, plano alimentar, avaliações, gasto energético, exames e documentos profissionais em uma rotina conectada.",
    destaque: "Do acompanhamento inicial ao plano alimentar com documentos e histórico por paciente.",
    cenario: "Plano alimentar ativo",
    capacidades: [
      "Prontuário nutricional por paciente",
      "Menu rápido para avaliação, exames e plano alimentar",
      "Avaliação antropométrica e gasto energético",
      "Plano alimentar com refeições, alimentos, suplementos e macros",
      "Solicitações, prescrições e documentos com carimbo profissional"
    ],
    documentos: ["Plano alimentar", "Solicitação de exames", "Prescrição nutricional", "Documento com CRN"],
    jornada: ["Paciente", "Avaliação", "Gasto energético", "Plano alimentar", "Documento"],
    metricas: [
      { rotulo: "Pacientes", valor: "5+" },
      { rotulo: "Refeições", valor: "5" },
      { rotulo: "Macros", valor: "kcal" }
    ]
  },
  {
    slug: "beauty-pro",
    nome: "Beauty Pro",
    categoria: "Estética e beleza",
    conselho: "Sem conselho obrigatório",
    resumo:
      "Ficha estética, protocolos, sessões, evolução segura, termos, produtos, agenda e precificação para estúdios e profissionais de beleza.",
    destaque: "Uma operação Beauty com segurança, margem e experiência visual profissional.",
    cenario: "Protocolo em andamento",
    capacidades: [
      "Ficha estética e anamnese com alertas",
      "Protocolos, pacotes e sessões",
      "Evolução segura sem fotos reais obrigatórias",
      "Termos de consentimento e produtos/lotes utilizados",
      "Agenda, serviços e precificação conectados"
    ],
    documentos: ["Termo de consentimento", "Registro de sessão", "Evidência segura", "Histórico do protocolo"],
    jornada: ["Cliente", "Ficha", "Protocolo", "Sessão", "Precificação"],
    metricas: [
      { rotulo: "Serviços", valor: "18" },
      { rotulo: "Alertas", valor: "1" },
      { rotulo: "Agenda", valor: "14d" }
    ]
  },
  {
    slug: "biomed-pro",
    nome: "Biomed Pro",
    categoria: "Biomedicina estética",
    conselho: "CRBM",
    resumo:
      "Base para rastreabilidade de procedimentos, produtos, lotes, habilitação profissional e documentos biomédicos.",
    destaque: "Rastreabilidade e responsabilidade técnica para procedimentos estéticos biomédicos.",
    cenario: "Procedimento rastreado",
    capacidades: [
      "Habilitação e identificação profissional",
      "Procedimentos estéticos com rastreabilidade",
      "Produtos e lotes vinculados ao atendimento",
      "Termos e documentos profissionais",
      "Base preparada para intercorrências e evolução"
    ],
    documentos: ["Termo biomédico", "Registro de lote", "Documento com CRBM", "Relatório de procedimento"],
    jornada: ["Cliente", "Avaliação", "Produto/lote", "Procedimento", "Documento"],
    metricas: [
      { rotulo: "Rastreio", valor: "lote" },
      { rotulo: "Conselho", valor: "CRBM" },
      { rotulo: "Status", valor: "ativo" }
    ]
  },
  {
    slug: "fisio-pro",
    nome: "Fisio Pro",
    categoria: "Fisioterapia",
    conselho: "CREFITO",
    resumo:
      "Avaliação funcional, plano terapêutico, evolução por sessão, pacotes e documentos profissionais para fisioterapia.",
    destaque: "Acompanhamento clínico e operacional para sessões recorrentes e evolução do paciente.",
    cenario: "Plano terapêutico",
    capacidades: [
      "Avaliação funcional inicial",
      "Plano terapêutico por paciente",
      "Evolução por sessão",
      "Pacotes de acompanhamento",
      "Relatórios e documentos com CREFITO"
    ],
    documentos: ["Relatório fisioterapêutico", "Plano terapêutico", "Evolução por sessão", "Documento com CREFITO"],
    jornada: ["Paciente", "Avaliação", "Plano", "Sessão", "Evolução"],
    metricas: [
      { rotulo: "Sessões", valor: "recorr." },
      { rotulo: "Conselho", valor: "CREFITO" },
      { rotulo: "Evolução", valor: "linha" }
    ]
  },
  {
    slug: "spaces",
    nome: "Spaces",
    categoria: "Sublocação e espaços",
    conselho: "Gestão de espaços",
    resumo:
      "Salas, cadeiras, cabines, equipamentos, pacotes de sublocação, ocupação e simulação de lucro para espaços compartilhados.",
    destaque: "Controle de recurso, custo por hora e ocupação para transformar espaços em operação rentável.",
    cenario: "Sala por hora",
    capacidades: [
      "Cadastro de recursos e ambientes",
      "Custo por hora de espaço",
      "Pacotes por hora, turno, diária ou mensal",
      "Simulação de parceiro",
      "Agenda de ocupação e relatório"
    ],
    documentos: ["Relatório de sublocação", "Simulação do parceiro", "Histórico de ocupação", "Resumo de recurso"],
    jornada: ["Recurso", "Custo/hora", "Pacote", "Ocupação", "Relatório"],
    metricas: [
      { rotulo: "Recursos", valor: "multi" },
      { rotulo: "Pacotes", valor: "5" },
      { rotulo: "Ocupação", valor: "%" }
    ]
  }
];

export function obterVerticalPublica(slug: string) {
  return verticaisPublicas.find((vertical) => vertical.slug === slug);
}
