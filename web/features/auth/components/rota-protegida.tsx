"use client";

import type { ReactNode } from "react";
import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import { LoaderCircle, ShieldCheck } from "lucide-react";

import { carregarSessaoAutenticada, type SessaoAutenticada } from "@/features/auth/lib/auth-storage";

type RotaProtegidaProps = {
  children: (sessao: SessaoAutenticada) => ReactNode;
};

export function RotaProtegida({ children }: RotaProtegidaProps) {
  const router = useRouter();
  const pathname = usePathname();
  const [sessao, setSessao] = useState<SessaoAutenticada | null>(null);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    const sessaoAtual = carregarSessaoAutenticada();
    if (!sessaoAtual) {
      const destino = encodeURIComponent(pathname || "/app");
      router.replace(`/login?redirectTo=${destino}`);
      return;
    }

    setSessao(sessaoAtual);
    setCarregando(false);
  }, [pathname, router]);

  if (carregando || !sessao) {
    return (
      <main className="flex min-h-screen items-center justify-center px-4">
        <div className="flex items-center gap-3 rounded-lg border bg-card px-4 py-3 text-sm font-medium text-card-foreground shadow-sm">
          <LoaderCircle className="h-4 w-4 animate-spin text-primary" />
          Validando acesso
        </div>
      </main>
    );
  }

  return <>{children(sessao)}</>;
}

export function RotaPublica({ children }: { children: ReactNode }) {
  const router = useRouter();
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    const sessaoAtual = carregarSessaoAutenticada();
    if (sessaoAtual) {
      router.replace("/app");
      return;
    }

    setCarregando(false);
  }, [router]);

  if (carregando) {
    return (
      <main className="flex min-h-screen items-center justify-center px-4">
        <div className="flex items-center gap-3 rounded-lg border bg-card px-4 py-3 text-sm font-medium text-card-foreground shadow-sm">
          <ShieldCheck className="h-4 w-4 text-primary" />
          Preparando acesso
        </div>
      </main>
    );
  }

  return <>{children}</>;
}
