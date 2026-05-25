export type PlanoPublico = {
  codigo: string;
  nome: string;
  preco: string;
  publico: string;
  destaque?: string;
  recomendado?: boolean;
  limites: string[];
  recursos: string[];
};

export const planosPublicos: PlanoPublico[] = [
  {
    codigo: "estudante",
    nome: "Estudante",
    preco: "R$ 29,90",
    publico: "Para validação acadêmica e treinamento.",
    destaque: "Documentos acadêmicos com marca d'água.",
    limites: ["1 usuário", "Pacientes limitados", "Documentos acadêmicos"],
    recursos: ["Núcleo operacional", "Verticais em modo estudo", "Sem documento oficial sem conselho"]
  },
  {
    codigo: "start",
    nome: "Start",
    preco: "R$ 79,90",
    publico: "Para profissionais solo começando a organizar a operação.",
    limites: ["1 usuário", "Clientes essenciais", "Agenda e serviços"],
    recursos: ["Cadastro de clientes", "Agenda base", "Procedimentos", "Custos iniciais"]
  },
  {
    codigo: "care",
    nome: "Care",
    preco: "R$ 119,90",
    publico: "Para consultórios que precisam de rotina e documentos.",
    recomendado: true,
    destaque: "Melhor entrada para operação profissional.",
    limites: ["Até 3 usuários", "Clientes ampliados", "Documentos profissionais"],
    recursos: ["Dashboard", "Documentos", "Precificação", "Relatórios iniciais"]
  },
  {
    codigo: "nutri-pro",
    nome: "Nutri Pro",
    preco: "R$ 149,90",
    publico: "Para nutricionistas e clínicas de nutrição.",
    limites: ["Módulo Nutri", "CRN e carimbo", "Plano alimentar"],
    recursos: ["Prontuário nutricional", "Avaliação e gasto energético", "Exames e prescrições"]
  },
  {
    codigo: "beauty-pro",
    nome: "Beauty Pro",
    preco: "R$ 149,90",
    publico: "Para estética, beleza e protocolos de atendimento.",
    limites: ["Módulo Beauty", "Protocolos e sessões", "Produtos e termos"],
    recursos: ["Ficha estética", "Evolução segura", "Agenda e precificação Beauty"]
  },
  {
    codigo: "biomed-pro",
    nome: "Biomed Pro",
    preco: "R$ 179,90",
    publico: "Para biomedicina estética com rastreabilidade.",
    limites: ["Módulo Biomed", "CRBM", "Produtos e lotes"],
    recursos: ["Rastreabilidade", "Documentos biomédicos", "Procedimentos estéticos"]
  },
  {
    codigo: "fisio-pro",
    nome: "Fisio Pro",
    preco: "R$ 149,90",
    publico: "Para fisioterapia, evolução e planos terapêuticos.",
    limites: ["Módulo Fisio", "CREFITO", "Sessões recorrentes"],
    recursos: ["Avaliação funcional", "Plano terapêutico", "Evolução por sessão"]
  },
  {
    codigo: "business",
    nome: "Business",
    preco: "R$ 249,90",
    publico: "Para equipes, clínicas e operações com múltiplos profissionais.",
    limites: ["Equipe ampliada", "Múltiplos profissionais", "Relatórios"],
    recursos: ["Admin da empresa", "Permissões", "Dashboard operacional", "Suporte priorizado"]
  },
  {
    codigo: "spaces",
    nome: "Spaces",
    preco: "R$ 299,90",
    publico: "Para sublocação, salas, cadeiras, cabines e recursos.",
    limites: ["Recursos ilimitados por operação", "Pacotes de sublocação", "Agenda de ocupação"],
    recursos: ["Custo por hora", "Simulação de parceiro", "Relatório de sublocação"]
  },
  {
    codigo: "premium",
    nome: "Premium",
    preco: "R$ 499,90",
    publico: "Para redes, gestão avançada e visão executiva.",
    destaque: "Plano para crescimento e governança.",
    limites: ["Operação avançada", "Módulos combinados", "Relatórios executivos"],
    recursos: ["Admin SaaS completo", "Suporte premium", "Múltiplas áreas", "Preparado para integrações futuras"]
  }
];

export const comparativoPlanos = [
  { recurso: "Trial 30 dias", start: true, care: true, pro: true, business: true, premium: true },
  { recurso: "Agenda e clientes", start: true, care: true, pro: true, business: true, premium: true },
  { recurso: "Precificação com alertas", start: false, care: true, pro: true, business: true, premium: true },
  { recurso: "Documentos profissionais", start: false, care: true, pro: true, business: true, premium: true },
  { recurso: "Vertical especializada", start: false, care: false, pro: true, business: true, premium: true },
  { recurso: "Equipe e permissões ampliadas", start: false, care: false, pro: false, business: true, premium: true },
  { recurso: "Visão executiva", start: false, care: false, pro: false, business: false, premium: true }
];
