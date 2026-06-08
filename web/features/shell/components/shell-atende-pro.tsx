"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import {
  Apple,
  BadgeDollarSign,
  CalendarDays,
  ClipboardList,
  FileText,
  Gauge,
  HeartHandshake,
  LayoutDashboard,
  PackageCheck,
  UserRound,
  Search,
  Sparkles,
  Stethoscope,
  Users
} from "lucide-react";

import { limparSessaoAutenticada, type SessaoAutenticada } from "@/features/auth/lib/auth-storage";
import { listarEmpresas } from "@/features/operacional/api/operacional-client";
import { BarraSuperiorContextual } from "@/features/shell/components/barra-superior-contextual";
import { MenuPrincipal } from "@/features/shell/components/menu-principal";
import { NavegacaoMobile } from "@/features/shell/components/navegacao-mobile";
import { PainelConteudoAtivo } from "@/features/shell/components/painel-conteudo-ativo";
import type { SecaoPrincipal, SecaoPrincipalConfig } from "@/features/shell/types";
import { resolverWorkspacePorPerfil } from "@/features/workspace/lib/resolver-workspace-por-perfil";

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
  },
  {
    id: "portal-cliente",
    label: "Portal do cliente",
    labelCurto: "Portal",
    titulo: "Portal do cliente",
    descricao: "Visualize agenda, documentos e evolução de clientes selecionados.",
    icon: UserRound
  }
];

const secoesNutriPro: SecaoPrincipalConfig[] = [
  {
    id: "nutri-inicio",
    label: "Início Nutri",
    labelCurto: "Início",
    titulo: "Nutri Pro",
    descricao: "Workspace da nutricionista para acompanhar pacientes, agenda, plano alimentar, exames e documentos.",
    icon: Apple
  },
  {
    id: "nutri-agenda",
    label: "Agenda",
    labelCurto: "Agenda",
    titulo: "Agenda Nutri",
    descricao: "Atendimentos nutricionais, retornos e próximos compromissos do acompanhamento.",
    icon: CalendarDays
  },
  {
    id: "nutri-pacientes",
    label: "Pacientes",
    labelCurto: "Pacientes",
    titulo: "Pacientes Nutri",
    descricao: "Lista, busca e seleção de pacientes para abrir o prontuário nutricional.",
    icon: Users
  },
  {
    id: "nutri-prontuario",
    label: "Prontuário",
    labelCurto: "Pront.",
    titulo: "Prontuário nutricional",
    descricao: "Central do paciente com resumo, ações rápidas e histórico nutricional.",
    icon: ClipboardList
  },
  {
    id: "nutri-plano",
    label: "Plano alimentar",
    labelCurto: "Plano",
    titulo: "Plano alimentar",
    descricao: "Fluxo de criação, histórico, refeições, alimentos e resumo de macronutrientes.",
    icon: Apple
  },
  {
    id: "nutri-avaliacoes",
    label: "Avaliações",
    labelCurto: "Aval.",
    titulo: "Avaliação e gasto energético",
    descricao: "Avaliação antropométrica, IMC, TMB, GEB, GET e apoio à conduta profissional.",
    icon: Gauge
  },
  {
    id: "nutri-documentos",
    label: "Exames e documentos",
    labelCurto: "Docs",
    titulo: "Exames, prescrições e documentos",
    descricao: "Solicitações, prescrições, PDFs e documentos profissionais com carimbo CRN.",
    icon: FileText
  },
  {
    id: "nutri-pos-venda",
    label: "Pós-venda",
    labelCurto: "Pós",
    titulo: "Pós-venda Nutri",
    descricao: "Retornos, adesão ao plano, check-ins, NPS, campanhas e reativação de pacientes.",
    icon: HeartHandshake
  },
  {
    id: "precificacao",
    label: "Precificação",
    labelCurto: "Preço",
    titulo: "Precificação Nutri",
    descricao: "Simulador de custo real e margem para serviços nutricionais.",
    icon: BadgeDollarSign
  },
  {
    id: "busca",
    label: "Busca global",
    labelCurto: "Busca",
    titulo: "Busca global",
    descricao: "Localize pacientes, agenda, documentos, serviços e custos sem perder o contexto do Nutri Pro.",
    icon: Search
  }
];

const secoesBeautyPro: SecaoPrincipalConfig[] = [
  {
    id: "beauty-inicio",
    label: "Início Beauty",
    labelCurto: "Início",
    titulo: "Beauty Pro",
    descricao: "Workspace profissional para estética, protocolos, sessões, produtos e documentos.",
    icon: Sparkles
  },
  {
    id: "beauty-agenda",
    label: "Agenda e preços",
    labelCurto: "Agenda",
    titulo: "Agenda e precificação Beauty",
    descricao: "Atendimentos, serviços, protocolos e simulações da operação Beauty Pro.",
    icon: CalendarDays
  },
  {
    id: "beauty-clientes",
    label: "Clientes",
    labelCurto: "Clientes",
    titulo: "Clientes Beauty",
    descricao: "Lista, busca e seleção de clientes para abrir ficha estética e histórico profissional.",
    icon: Users
  },
  {
    id: "beauty-ficha",
    label: "Ficha estética",
    labelCurto: "Ficha",
    titulo: "Ficha estética",
    descricao: "Anamnese, contraindicações, segurança e histórico estético do cliente.",
    icon: ClipboardList
  },
  {
    id: "beauty-protocolos",
    label: "Protocolos e sessões",
    labelCurto: "Protocolos",
    titulo: "Protocolos e sessões",
    descricao: "Pacotes estéticos, execução de sessões, evolução e histórico por cliente.",
    icon: Sparkles
  },
  {
    id: "beauty-estoque",
    label: "Estoque Beauty",
    labelCurto: "Estoque",
    titulo: "Estoque Beauty",
    descricao: "Produtos, lotes, validade, baixo estoque, baixas e kits por procedimento.",
    icon: PackageCheck
  },
  {
    id: "beauty-pos-venda",
    label: "Pós-venda",
    labelCurto: "Pós",
    titulo: "Pós-venda Beauty",
    descricao: "Pós-procedimento, manutenção, pacotes, NPS, campanhas e recorrência em estética.",
    icon: HeartHandshake
  },
  {
    id: "beauty-termos",
    label: "Termos e produtos",
    labelCurto: "Termos",
    titulo: "Termos, evidências e produtos",
    descricao: "Consentimentos, evidências seguras, produtos/lotes e rastreabilidade operacional.",
    icon: FileText
  },
  {
    id: "precificacao",
    label: "Precificação",
    labelCurto: "Preço",
    titulo: "Precificação Beauty",
    descricao: "Simulador de custo real e margem para estética e beleza.",
    icon: BadgeDollarSign
  },
  {
    id: "busca",
    label: "Busca global",
    labelCurto: "Busca",
    titulo: "Busca global",
    descricao: "Localize clientes, agenda, serviços, estoque, equipamentos e custos.",
    icon: Search
  }
];

export function ShellAtendePro({ sessao }: ShellAtendeProProps) {
  const router = useRouter();
  const workspaceProfissional = resolverWorkspacePorPerfil(sessao.usuario);
  const secoesDisponiveis = useMemo(() => {
    if (workspaceProfissional?.tipo === "NUTRI_PRO") {
      return secoesNutriPro;
    }
    if (workspaceProfissional?.tipo === "BEAUTY_PRO") {
      return secoesBeautyPro;
    }
    return secoesPrincipais;
  }, [workspaceProfissional?.tipo]);

  const [secaoAtiva, definirSecaoAtiva] = useState<SecaoPrincipal>(() => secoesDisponiveis[0].id);
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

  useEffect(() => {
    if (!secoesDisponiveis.some((secao) => secao.id === secaoAtiva)) {
      definirSecaoAtiva(secoesDisponiveis[0].id);
    }
  }, [secaoAtiva, secoesDisponiveis]);

  const empresaAtivaId = sessao.usuario.empresaId ?? empresaSelecionadaId;
  const empresas = empresasQuery.data?.itens ?? [];
  const empresaSelecionada = empresas.find((empresa) => empresa.id === empresaAtivaId);
  const empresaAtualNome = empresaSelecionada?.nomeFantasia ?? (sessao.usuario.empresaId ? "Empresa atual" : "Admin SaaS");
  const secaoAtual = secoesDisponiveis.find((secao) => secao.id === secaoAtiva) ?? secoesDisponiveis[0];

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
              <p className="text-sm font-semibold text-card-foreground">{workspaceProfissional?.nome ?? "AtendePro"}</p>
              <p className="text-xs font-medium text-muted-foreground">{workspaceProfissional ? "Workspace profissional" : "SaaS profissional"}</p>
            </div>
          </div>

          <div className="mt-8">
            <MenuPrincipal secoes={secoesDisponiveis} secaoAtiva={secaoAtiva} definirSecaoAtiva={definirSecaoAtiva} />
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
          <PainelConteudoAtivo secaoAtiva={secaoAtiva} empresaId={empresaAtivaId} sessao={sessao} definirSecaoAtiva={definirSecaoAtiva} />
        </div>
      </div>

      <NavegacaoMobile secoes={secoesDisponiveis} secaoAtiva={secaoAtiva} definirSecaoAtiva={definirSecaoAtiva} />
    </main>
  );
}

function normalizarPerfil(perfil?: string) {
  return perfil?.replace("_", " ") ?? "Acesso";
}
