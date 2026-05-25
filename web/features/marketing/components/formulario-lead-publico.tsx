"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { ArrowLeft, ArrowRight, BadgeCheck, Loader2, Send } from "lucide-react";
import {
  registrarLeadMarketing,
  type AreaInteresseLead,
  type LeadMarketingResponse,
  type TamanhoOperacaoLead
} from "@/features/marketing/api/leads-client";

type EstadoEnvio = "inicial" | "enviando" | "sucesso" | "erro";

type DadosFormularioLead = {
  nome: string;
  email: string;
  telefone: string;
  areaInteresse: AreaInteresseLead;
  tamanhoOperacao: TamanhoOperacaoLead;
  mensagem: string;
};

const dadosIniciais: DadosFormularioLead = {
  nome: "",
  email: "",
  telefone: "",
  areaInteresse: "NUTRI_PRO",
  tamanhoOperacao: "PROFISSIONAL_SOLO",
  mensagem: ""
};

const areasInteresse: Array<{ valor: AreaInteresseLead; rotulo: string }> = [
  { valor: "NUTRI_PRO", rotulo: "Nutri Pro" },
  { valor: "BEAUTY_PRO", rotulo: "Beauty Pro" },
  { valor: "BIOMED_PRO", rotulo: "Biomed Pro" },
  { valor: "FISIO_PRO", rotulo: "Fisio Pro" },
  { valor: "SPACES", rotulo: "Spaces" },
  { valor: "OUTRA", rotulo: "Outra área" }
];

const tamanhosOperacao: Array<{ valor: TamanhoOperacaoLead; rotulo: string }> = [
  { valor: "PROFISSIONAL_SOLO", rotulo: "Profissional solo" },
  { valor: "EQUIPE_PEQUENA", rotulo: "Equipe pequena" },
  { valor: "CLINICA", rotulo: "Clínica ou estúdio" },
  { valor: "MULTIUNIDADE", rotulo: "Multiunidade" },
  { valor: "ESTUDANTE", rotulo: "Estudante" }
];

export function FormularioLeadPublico() {
  const [dados, setDados] = useState<DadosFormularioLead>(dadosIniciais);
  const [estado, setEstado] = useState<EstadoEnvio>("inicial");
  const [leadRegistrado, setLeadRegistrado] = useState<LeadMarketingResponse | null>(null);

  async function enviarFormulario(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setEstado("enviando");
    setLeadRegistrado(null);

    try {
      const response = await registrarLeadMarketing({
        ...dados,
        origem: "formulario-publico-r10",
        telefone: dados.telefone || undefined,
        mensagem: dados.mensagem || undefined
      });
      setLeadRegistrado(response);
      setEstado("sucesso");
      setDados(dadosIniciais);
    } catch {
      setEstado("erro");
    }
  }

  return (
    <main className="min-h-screen bg-[#f5fbf8] text-[#102524]">
      <section className="border-b border-[#cddfda] bg-[#edf7f3]">
        <div className="mx-auto max-w-6xl px-4 pb-10 pt-5 sm:px-6 lg:px-8">
          <header className="flex items-center justify-between gap-4">
            <Link
              href="/"
              className="inline-flex items-center gap-2 rounded-md text-sm font-semibold text-[#123c3a] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
            >
              <ArrowLeft className="h-4 w-4" aria-hidden="true" />
              AtendePro
            </Link>
            <Link
              href="/login"
              className="inline-flex h-10 items-center justify-center gap-2 rounded-md border border-[#a8cbc4] bg-white px-4 text-sm font-semibold text-[#123c3a] shadow-sm hover:bg-[#f8fcfb] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
            >
              Entrar
              <ArrowRight className="h-4 w-4" aria-hidden="true" />
            </Link>
          </header>
          <div className="mt-10 grid gap-6 lg:grid-cols-[0.86fr_1.14fr] lg:items-start">
            <div>
              <p className="inline-flex items-center gap-2 rounded-full border border-[#b9d8d1] bg-white/80 px-3 py-1 text-sm font-semibold text-[#0f766e] shadow-sm">
                <Send className="h-4 w-4" aria-hidden="true" />
                Demonstração e interesse comercial
              </p>
              <h1 className="mt-5 text-4xl font-semibold leading-[1.05] text-[#102524] sm:text-5xl lg:text-6xl">
                Solicitar contato
              </h1>
              <p className="mt-4 max-w-2xl text-base leading-7 text-[#405a56]">
                Registre seu interesse para testar o AtendePro com dados demo e conversar sobre o melhor módulo para a sua operação.
              </p>
              <div className="mt-6 rounded-lg border border-[#d7e5e1] bg-white/80 p-4">
                <p className="text-sm font-semibold text-[#102524]">Sem integração externa nesta fase</p>
                <p className="mt-2 text-sm leading-6 text-[#536b67]">
                  O lead é salvo no backend local do AtendePro. CRM, automações e e-mail ficam para releases futuras.
                </p>
              </div>
            </div>
            <form onSubmit={enviarFormulario} className="rounded-lg border border-[#d7e5e1] bg-white p-5 shadow-lg shadow-[#0f2f2b]/10">
              <div className="grid gap-4 sm:grid-cols-2">
                <label className="block sm:col-span-2">
                  <span className="text-sm font-medium text-[#244641]">Nome</span>
                  <input
                    required
                    value={dados.nome}
                    onChange={(event) => setDados((atual) => ({ ...atual, nome: event.target.value }))}
                    className="mt-2 min-h-11 w-full rounded-md border border-[#cfe0dc] bg-white px-3 text-sm font-semibold text-[#102524] outline-none focus:ring-2 focus:ring-[#0f766e]"
                    placeholder="Seu nome"
                  />
                </label>
                <label className="block">
                  <span className="text-sm font-medium text-[#244641]">E-mail</span>
                  <input
                    required
                    type="email"
                    value={dados.email}
                    onChange={(event) => setDados((atual) => ({ ...atual, email: event.target.value }))}
                    className="mt-2 min-h-11 w-full rounded-md border border-[#cfe0dc] bg-white px-3 text-sm font-semibold text-[#102524] outline-none focus:ring-2 focus:ring-[#0f766e]"
                    placeholder="voce@empresa.com"
                  />
                </label>
                <label className="block">
                  <span className="text-sm font-medium text-[#244641]">Telefone</span>
                  <input
                    value={dados.telefone}
                    onChange={(event) => setDados((atual) => ({ ...atual, telefone: event.target.value }))}
                    className="mt-2 min-h-11 w-full rounded-md border border-[#cfe0dc] bg-white px-3 text-sm font-semibold text-[#102524] outline-none focus:ring-2 focus:ring-[#0f766e]"
                    placeholder="Opcional"
                  />
                </label>
                <label className="block">
                  <span className="text-sm font-medium text-[#244641]">Área de interesse</span>
                  <select
                    value={dados.areaInteresse}
                    onChange={(event) =>
                      setDados((atual) => ({ ...atual, areaInteresse: event.target.value as AreaInteresseLead }))
                    }
                    className="mt-2 min-h-11 w-full rounded-md border border-[#cfe0dc] bg-white px-3 text-sm font-semibold text-[#102524] outline-none focus:ring-2 focus:ring-[#0f766e]"
                  >
                    {areasInteresse.map((area) => (
                      <option key={area.valor} value={area.valor}>
                        {area.rotulo}
                      </option>
                    ))}
                  </select>
                </label>
                <label className="block">
                  <span className="text-sm font-medium text-[#244641]">Tamanho da operação</span>
                  <select
                    value={dados.tamanhoOperacao}
                    onChange={(event) =>
                      setDados((atual) => ({ ...atual, tamanhoOperacao: event.target.value as TamanhoOperacaoLead }))
                    }
                    className="mt-2 min-h-11 w-full rounded-md border border-[#cfe0dc] bg-white px-3 text-sm font-semibold text-[#102524] outline-none focus:ring-2 focus:ring-[#0f766e]"
                  >
                    {tamanhosOperacao.map((tamanho) => (
                      <option key={tamanho.valor} value={tamanho.valor}>
                        {tamanho.rotulo}
                      </option>
                    ))}
                  </select>
                </label>
                <label className="block sm:col-span-2">
                  <span className="text-sm font-medium text-[#244641]">Mensagem</span>
                  <textarea
                    rows={4}
                    value={dados.mensagem}
                    onChange={(event) => setDados((atual) => ({ ...atual, mensagem: event.target.value }))}
                    className="mt-2 w-full rounded-md border border-[#cfe0dc] bg-white px-3 py-3 text-sm font-semibold text-[#102524] outline-none focus:ring-2 focus:ring-[#0f766e]"
                    placeholder="Conte rapidamente sua área, cidade ou principal necessidade."
                  />
                </label>
              </div>
              <button
                type="submit"
                disabled={estado === "enviando"}
                className="mt-5 inline-flex h-12 w-full items-center justify-center gap-2 rounded-md bg-[#0f766e] px-5 text-sm font-semibold text-white shadow-lg shadow-[#0f766e]/20 transition hover:bg-[#0d625c] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e] disabled:cursor-not-allowed disabled:opacity-70"
              >
                {estado === "enviando" ? <Loader2 className="h-4 w-4 animate-spin" aria-hidden="true" /> : <Send className="h-4 w-4" aria-hidden="true" />}
                Enviar interesse
              </button>
              {estado === "sucesso" && leadRegistrado ? (
                <div className="mt-4 rounded-lg border border-[#b6dfcf] bg-[#f0fbf6] p-4 text-sm text-[#0f5f59]">
                  <div className="flex items-center gap-2 font-semibold">
                    <BadgeCheck className="h-4 w-4" aria-hidden="true" />
                    Interesse registrado
                  </div>
                  <p className="mt-2">Lead local criado com status {leadRegistrado.status}. ID: {leadRegistrado.id}</p>
                </div>
              ) : null}
              {estado === "erro" ? (
                <div className="mt-4 rounded-lg border border-[#f0b7a8] bg-[#fff1ed] p-4 text-sm font-semibold text-[#9a3412]">
                  Não foi possível registrar seu interesse agora. Confira a conexão local e tente novamente.
                </div>
              ) : null}
            </form>
          </div>
        </div>
      </section>
    </main>
  );
}
