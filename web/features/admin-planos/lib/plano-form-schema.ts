import { z } from "zod";

export const planoFormSchema = z.object({
  codigo: z.string().trim().min(2, "Código obrigatório"),
  nome: z.string().trim().min(2, "Nome obrigatório"),
  descricao: z.string().trim().optional(),
  valorMensal: z.coerce.number().min(0, "Valor inválido"),
  limiteUsuarios: z.coerce.number().int().min(0, "Limite inválido"),
  limiteClientes: z.coerce.number().int().min(0, "Limite inválido"),
  limiteProfissionais: z.coerce.number().int().min(0, "Limite inválido"),
  ativo: z.boolean(),
  estudante: z.boolean(),
  marcaDaguaAcademica: z.string().trim().optional(),
  modulosTexto: z.string().trim().min(3, "Informe ao menos um módulo")
});

export type PlanoFormData = z.infer<typeof planoFormSchema>;
