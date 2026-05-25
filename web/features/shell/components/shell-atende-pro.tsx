"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { BadgeDollarSign, LayoutDashboard, PackageCheck, Search, Stethoscope } from "lucide-react";

import { limparSessaoAutenticada, type SessaoAutenticada } from "@/features/auth/lib/auth-storage";
import { listarEmpresas } from "@/features/operacional/api/operacional-client";
import { BarraSuperiorContextual } from "@/features/shell/components/barra-superior-contextual";
import { MenuPrincipal } from "@/features/shell/components/menu-principal";
import { NavegacaoMobile } from "@/features/shell/components/navegacao-mobile";
import { PainelConteudoAtivo } from "@/features/shell/components/painel-conteudo-ativo";
import type { SecaoPrincipal, SecaoPrincipalConfig } from "@/features/shell/types";

type ShellAtendeProProps = {
  sessao: SessaoAutenticada;
};

const secoesPrincipais: SecaoPrincipalConfig[] = [
  {
    id: "operacao",
    label: "Operação",
    labelCurto: "Operar",
    titulo: "Painel operacional",
    descricao: "Indicadores e alertas do núcleo comum para acompanhar a rotina da empresa.",
    icon: LayoutDashboard
  },
  {
    id: "verticais",
    label: "Verticais",
    labelCurto: "Áreas",
    titulo: "Verticais profissionais",
    descricao: "Módulos por área com detalhe sob demanda, sem empilhar todas as capacidades na mesma página.",
    icon: Stethoscope
  },
  {
    id: "precificacao",
    label: "Precificação",
    labelCurto: "Preço",
    titulo: "Precificação",
    descricao: "Simulador de custo real, margem e histórico de preços em uma área própria.",
    icon: BadgeDollarSign
  },
  {
    id: "busca",
    label: "Busca global",
    labelCurto: "Busca",
    titulo: "Busca global",
    descricao: "Localize clientes, agenda, serviços, estoque, equipamentos e custos sem sair do contexto ativo.",
    icon: Search
  },
  {
    id: "admin",
    label: "Admin SaaS",
    labelCurto: "Admin",
    titulo: "Admin SaaS",
    descricao: "Área administrativa para planos, limites e evoluções futuras de empresas e assinaturas.",
    icon: PackageCheck
  }
];

export function ShellAtendePro({ sessao }: ShellAtendeProProps) {
  const router = useRouter();
  const [secaoAtiva, definirSecaoAtiva] = useState<SecaoPrincipal>("operacao");
  const [empresaSelecionadaId, setEmpresaSelecionadaId] = useState(sessao.usuario.empresaId ?? "");

  const perfilPrincipal = useMemo(() => normalizarPerfil(sessao.usuario.perfis.at(0)), [sessao]);
  const ehUsuarioSaas = !sessao.usuario.empresaId;

  const empresasQuery = useQuery({
    queryKey: ["empresas-operacionais"],
    queryFn: () => listarEmpresas({ pagina: 0, tamanho: 20 }),
    enabled: ehUsuarioSaas
  });

  useEffect(() => {
    if (!empresaSelecionadaId && empresasQuery.data?.itens.length) {
      setEmpresaSelecionadaId(empresasQuery.data.itens[0].id);
    }
  }, [empresaSelecionadaId, empresasQuery.data]);

  const empresaAtivaId = sessao.usuario.empresaId ?? empresaSelecionadaId;
  const empresas = empresasQuery.data?.itens ?? [];
  const empresaSelecionada = empresas.find((empresa) => empresa.id === empresaAtivaId);
  const empresaAtualNome = empresaSelecionada?.nomeFantasia ?? (sessao.usuario.empresaId ? "Empresa atual" : "Admin SaaS");
  const secaoAtual = secoesPrincipais.find((secao) => secao.id === secaoAtiva) ?? secoesPrincipais[0];

  function sair() {
    limparSessaoAutenticada();
    router.replace("/login");
  }

  return (
    <main className="min-h-screen bg-background">
      <div className="mx-auto grid min-h-screen w-full max-w-[1600px] md:grid-cols-[88px_minmax(0,1fr)] xl:grid-cols-[280px_minmax(0,1fr)]">
        <aside className="sticky top-0 hidden h-screen border-r bg-card/90 px-3 py-5 shadow-sm backdrop-blur md:block xl:px-5">
          <div className="flex items-center justify-center gap-3 xl:justify-start">
            <span className="flex h-11 w-11 items-center justify-center rounded-md bg-primary text-primary-foreground shadow-sm">
              <Stethoscope className="h-5 w-5" />
            </span>
            <div className="hidden xl:block">
              <p className="text-sm font-semibold text-card-foreground">AtendePro</p>
              <p className="text-xs font-medium text-muted-foreground">SaaS profissional</p>
            </div>
          </div>

          <div className="mt-8">
            <MenuPrincipal secoes={secoesPrincipais} secaoAtiva={secaoAtiva} definirSecaoAtiva={definirSecaoAtiva} />
          </div>
        </aside>

        <div className="min-w-0 pb-24 md:pb-0">
          <BarraSuperiorContextual
            sessao={sessao}
            secao={secaoAtual}
            perfilPrincipal={perfilPrincipal}
            ehUsuarioSaas={ehUsuarioSaas}
            empresas={empresas}
            empresaSelecionadaId={empresaSelecionadaId}
            empresaAtualNome={empresaAtualNome}
            onEmpresaChange={setEmpresaSelecionadaId}
            onSair={sair}
          />
          <PainelConteudoAtivo secaoAtiva={secaoAtiva} empresaId={empresaAtivaId} sessao={sessao} />
        </div>
      </div>

      <NavegacaoMobile secoes={secoesPrincipais} secaoAtiva={secaoAtiva} definirSecaoAtiva={definirSecaoAtiva} />
    </main>
  );
}

function normalizarPerfil(perfil?: string) {
  return perfil?.replace("_", " ") ?? "Acesso";
}
