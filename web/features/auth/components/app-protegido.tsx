"use client";

import { PainelProtegidoView } from "@/features/auth/components/painel-protegido-view";
import { RotaProtegida } from "@/features/auth/components/rota-protegida";

export function AppProtegido() {
  return <RotaProtegida>{(sessao) => <PainelProtegidoView sessao={sessao} />}</RotaProtegida>;
}
