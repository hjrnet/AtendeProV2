import { z } from "zod";

export const planoFormSchema = z.object({
  codigo: z.string().trim().min(2, "Codigo obrigatorio"),
  nome: z.string().trim().min(2, "Nome obrigatorio"),
  descricao: z.string().trim().optional(),
  valorMensal: z.coerce.number().min(0, "Valor invalido"),
  limiteUsuarios: z.coerce.number().int().min(0, "Limite invalido"),
  limiteClientes: z.coerce.number().int().min(0, "Limite invalido"),
  limiteProfissionais: z.coerce.number().int().min(0, "Limite invalido"),
  ativo: z.boolean(),
  estudante: z.boolean(),
  marcaDaguaAcademica: z.string().trim().optional(),
  modulosTexto: z.string().trim().min(3, "Informe ao menos um modulo")
});

export type PlanoFormData = z.infer<typeof planoFormSchema>;
