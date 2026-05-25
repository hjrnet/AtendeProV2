import { z } from "zod";

export const loginSchema = z.object({
  email: z.string().trim().email("Informe um email valido."),
  senha: z.string().min(1, "Informe a senha.")
});

export type LoginFormData = z.infer<typeof loginSchema>;
