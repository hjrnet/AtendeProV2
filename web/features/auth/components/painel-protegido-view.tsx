"use client";

import { ShellAtendePro } from "@/features/shell/components/shell-atende-pro";
import type { SessaoAutenticada } from "@/features/auth/lib/auth-storage";

type PainelProtegidoViewProps = {
  sessao: SessaoAutenticada;
};

export function PainelProtegidoView({ sessao }: PainelProtegidoViewProps) {
  return <ShellAtendePro sessao={sessao} />;
}
