import type { UsuarioLogin } from "@/features/auth/api/auth-client";

export type TipoWorkspaceProfissional = "NUTRI_PRO" | "BEAUTY_PRO";

export type WorkspaceProfissionalResolvido = {
  tipo: TipoWorkspaceProfissional;
  nome: string;
  descricao: string;
};

export function resolverWorkspacePorPerfil(usuario: UsuarioLogin): WorkspaceProfissionalResolvido | null {
  const email = usuario.email.toLowerCase();
  const nome = usuario.nome.toLowerCase();

  if (email.includes("nutri") || nome.includes("nutricionista")) {
    return {
      tipo: "NUTRI_PRO",
      nome: "Nutri Pro",
      descricao: "Workspace profissional para atendimento nutricional, pacientes, planos, exames e documentos."
    };
  }

  if (email.includes("estetica") || email.includes("beauty") || nome.includes("esteticista")) {
    return {
      tipo: "BEAUTY_PRO",
      nome: "Beauty Pro",
      descricao: "Workspace profissional para estética, protocolos, sessões, produtos, agenda e precificação."
    };
  }

  return null;
}
