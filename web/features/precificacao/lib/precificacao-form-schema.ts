import { z } from "zod";

export const precificacaoFormSchema = z.object({
  nomeProcedimento: z.string().trim().min(2, "Nome obrigatorio"),
  duracaoMinutos: z.coerce.number().int().min(1, "Duracao invalida"),
  custoInsumos: z.coerce.number().min(0, "Valor invalido"),
  custoSalaPorHora: z.coerce.number().min(0, "Valor invalido"),
  valorHoraProfissional: z.coerce.number().min(0, "Valor invalido"),
  custoDeslocamento: z.coerce.number().min(0, "Valor invalido"),
  custoAlimentacao: z.coerce.number().min(0, "Valor invalido"),
  taxas: z.coerce.number().min(0, "Valor invalido"),
  margemDesejadaPercentual: z.coerce.number().min(0, "Margem invalida").max(99.99, "Margem invalida"),
  precoVenda: z.coerce.number().min(0.01, "Preco invalido")
});

export type PrecificacaoFormData = z.infer<typeof precificacaoFormSchema>;
