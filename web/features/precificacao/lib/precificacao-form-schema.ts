import { z } from "zod";

export const precificacaoFormSchema = z.object({
  nomeProcedimento: z.string().trim().min(2, "Nome obrigatório"),
  duracaoMinutos: z.coerce.number().int().min(1, "Duração inválida"),
  custoInsumos: z.coerce.number().min(0, "Valor inválido"),
  custoSalaPorHora: z.coerce.number().min(0, "Valor inválido"),
  valorHoraProfissional: z.coerce.number().min(0, "Valor inválido"),
  custoDeslocamento: z.coerce.number().min(0, "Valor inválido"),
  custoAlimentacao: z.coerce.number().min(0, "Valor inválido"),
  taxas: z.coerce.number().min(0, "Valor inválido"),
  margemDesejadaPercentual: z.coerce.number().min(0, "Margem inválida").max(99.99, "Margem inválida"),
  precoVenda: z.coerce.number().min(0.01, "Preço inválido")
});

export type PrecificacaoFormData = z.infer<typeof precificacaoFormSchema>;
