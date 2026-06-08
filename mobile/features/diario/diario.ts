export type TipoRegistro = "evolução" | "recado" | "foto";

export type RegistroDiario = {
  id: string;
  titulo: string;
  autor: string;
  data: string;
  hora: string;
  tipo: TipoRegistro;
  mensagem: string;
};

export const registros: RegistroDiario[] = [
  {
    id: "reg-01",
    titulo: "Paciente relata melhora de sintomas",
    autor: "Karol",
    data: "12/05/2026",
    hora: "09:14",
    tipo: "evolução",
    mensagem:
      "Ajuste de carboidratos no pós-treino sem sintomas de hipoglicemia. Manter hidratação."
  },
  {
    id: "reg-02",
    titulo: "Observação importante",
    autor: "Assistente",
    data: "10/05/2026",
    hora: "17:45",
    tipo: "recado",
    mensagem: "Lembrete: retornar exame de creatinina ainda não foi anexado."
  },
  {
    id: "reg-03",
    titulo: "Série comparativa",
    autor: "App",
    data: "08/05/2026",
    hora: "07:55",
    tipo: "foto",
    mensagem: "Nova foto anexada no módulo de evidências, 03ª tomada."
  }
];

export const limiteRegistros = 3;
