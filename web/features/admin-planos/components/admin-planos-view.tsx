"use client";

import type { InputHTMLAttributes } from "react";
import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm, type UseFormRegisterReturn } from "react-hook-form";
import { CheckCircle2, ChevronLeft, ChevronRight, Edit3, LoaderCircle, PackageCheck, Plus, Search, XCircle } from "lucide-react";

import { Button } from "@/components/ui/button";
import { ApiError } from "@/lib/api";
import {
  atualizarPlano,
  criarPlano,
  listarPlanos,
  type Plano,
  type SalvarPlanoRequest
} from "@/features/admin-planos/api/planos-client";
import { planoFormSchema, type PlanoFormData } from "@/features/admin-planos/lib/plano-form-schema";

const TAMANHO_PAGINA = 10;
const VALORES_INICIAIS: PlanoFormData = {
  codigo: "",
  nome: "",
  descricao: "",
  valorMensal: 0,
  limiteUsuarios: 1,
  limiteClientes: 30,
  limiteProfissionais: 1,
  ativo: true,
  estudante: false,
  marcaDaguaAcademica: "",
  modulosTexto: "clientes,agenda,dashboard"
};

export function AdminPlanosView() {
  const queryClient = useQueryClient();
  const [busca, setBusca] = useState("");
  const [pagina, setPagina] = useState(0);
  const [planoEditando, setPlanoEditando] = useState<Plano | null>(null);
  const [erro, setErro] = useState<string | null>(null);
  const form = useForm<PlanoFormData>({ defaultValues: VALORES_INICIAIS });

  const planosQuery = useQuery({
    queryKey: ["admin-planos", pagina, busca],
    queryFn: () => listarPlanos({ pagina, tamanho: TAMANHO_PAGINA, busca })
  });

  const salvarMutation = useMutation({
    mutationFn: (request: SalvarPlanoRequest) =>
      planoEditando ? atualizarPlano(planoEditando.id, request) : criarPlano(request),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin-planos"] });
      limparFormulario();
    },
    onError: (error) => setErro(error instanceof ApiError ? error.message : "Não foi possível salvar o plano.")
  });

  const totalPaginas = planosQuery.data?.totalPaginas ?? 0;
  const podeVoltar = pagina > 0;
  const podeAvancar = totalPaginas > 0 && pagina + 1 < totalPaginas;
  const planos = planosQuery.data?.itens ?? [];

  const totalAtivos = useMemo(() => planos.filter((plano) => plano.ativo).length, [planos]);

  const salvar = form.handleSubmit((valores) => {
    setErro(null);
    form.clearErrors();
    const validacao = planoFormSchema.safeParse(valores);

    if (!validacao.success) {
      validacao.error.issues.forEach((issue) => {
        const campo = issue.path.at(0) as keyof PlanoFormData | undefined;
        if (campo) {
          form.setError(campo, { message: issue.message });
        }
      });
      return;
    }

    const dados = validacao.data;
    salvarMutation.mutate({
      codigo: dados.codigo,
      nome: dados.nome,
      descricao: dados.descricao || null,
      valorMensal: dados.valorMensal,
      limiteUsuarios: dados.limiteUsuarios,
      limiteClientes: dados.limiteClientes,
      limiteProfissionais: dados.limiteProfissionais,
      ativo: dados.ativo,
      estudante: dados.estudante,
      marcaDaguaAcademica: dados.marcaDaguaAcademica || null,
      modulos: dados.modulosTexto
        .split(",")
        .map((modulo) => modulo.trim())
        .filter(Boolean)
    });
  });

  function editarPlano(plano: Plano) {
    setPlanoEditando(plano);
    setErro(null);
    form.reset({
      codigo: plano.codigo,
      nome: plano.nome,
      descricao: plano.descricao ?? "",
      valorMensal: plano.valorMensal,
      limiteUsuarios: plano.limiteUsuarios,
      limiteClientes: plano.limiteClientes,
      limiteProfissionais: plano.limiteProfissionais,
      ativo: plano.ativo,
      estudante: plano.estudante,
      marcaDaguaAcademica: plano.marcaDaguaAcademica ?? "",
      modulosTexto: plano.modulos.join(",")
    });
  }

  function limparFormulario() {
    setPlanoEditando(null);
    setErro(null);
    form.reset(VALORES_INICIAIS);
  }

  function alternarAtivo(plano: Plano) {
    setErro(null);
    salvarMutation.mutate({
      codigo: plano.codigo,
      nome: plano.nome,
      descricao: plano.descricao,
      valorMensal: plano.valorMensal,
      limiteUsuarios: plano.limiteUsuarios,
      limiteClientes: plano.limiteClientes,
      limiteProfissionais: plano.limiteProfissionais,
      ativo: !plano.ativo,
      estudante: plano.estudante,
      marcaDaguaAcademica: plano.marcaDaguaAcademica,
      modulos: plano.modulos
    });
  }

  return (
    <section className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_380px]">
      <div className="min-w-0 rounded-lg border bg-card p-4 shadow-sm">
        <div className="flex flex-col gap-3 border-b pb-4 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-sm font-medium text-primary">Admin SaaS</p>
            <h2 className="mt-1 text-xl font-semibold text-card-foreground">Planos e limites</h2>
          </div>
          <div className="grid grid-cols-2 gap-2 sm:flex">
            <span className="rounded-md border bg-background px-3 py-2 text-sm font-semibold text-card-foreground">
              {planosQuery.data?.totalItens ?? 0} planos
            </span>
            <span className="rounded-md border bg-background px-3 py-2 text-sm font-semibold text-card-foreground">
              {totalAtivos} ativos
            </span>
          </div>
        </div>

        <div className="mt-4 flex flex-col gap-3 sm:flex-row sm:items-center">
          <label className="relative min-w-0 flex-1">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <input
              value={busca}
              onChange={(event) => {
                setBusca(event.target.value);
                setPagina(0);
              }}
              className="h-10 w-full rounded-md border bg-background pl-10 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Buscar por código ou nome"
            />
          </label>
          <div className="flex gap-2">
            <Button type="button" variant="outline" size="icon" onClick={() => setPagina((valor) => valor - 1)} disabled={!podeVoltar} title="Página anterior">
              <ChevronLeft className="h-4 w-4" />
            </Button>
            <Button type="button" variant="outline" size="icon" onClick={() => setPagina((valor) => valor + 1)} disabled={!podeAvancar} title="Próxima página">
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>

        <div className="mt-4 max-h-[620px] overflow-y-auto pr-1">
          {planosQuery.isLoading ? (
            <div className="flex min-h-44 items-center justify-center rounded-lg border bg-background text-sm text-muted-foreground">
              <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
              Carregando planos
            </div>
          ) : planos.length === 0 ? (
            <div className="flex min-h-44 flex-col items-center justify-center rounded-lg border bg-background p-6 text-center">
              <PackageCheck className="h-8 w-8 text-primary" />
              <p className="mt-3 text-sm font-semibold text-card-foreground">Nenhum plano encontrado</p>
            </div>
          ) : (
            <div className="grid gap-3">
              {planos.map((plano) => (
                <article key={plano.id} className="rounded-lg border bg-background p-4">
                  <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
                    <div className="min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <h3 className="text-base font-semibold text-card-foreground">{plano.nome}</h3>
                        <span className="rounded-md bg-primary/10 px-2 py-1 text-xs font-semibold text-primary">
                          {plano.codigo}
                        </span>
                        <span className="rounded-md border px-2 py-1 text-xs font-semibold text-muted-foreground">
                          {plano.ativo ? "Ativo" : "Inativo"}
                        </span>
                      </div>
                      <p className="mt-2 line-clamp-2 text-sm text-muted-foreground">{plano.descricao ?? "Sem descrição"}</p>
                    </div>
                    <div className="flex shrink-0 gap-2">
                      <Button type="button" variant="outline" size="icon" onClick={() => editarPlano(plano)} title="Editar plano">
                        <Edit3 className="h-4 w-4" />
                      </Button>
                      <Button type="button" variant="outline" size="icon" onClick={() => alternarAtivo(plano)} title={plano.ativo ? "Desativar plano" : "Ativar plano"}>
                        {plano.ativo ? <XCircle className="h-4 w-4" /> : <CheckCircle2 className="h-4 w-4" />}
                      </Button>
                    </div>
                  </div>

                  <div className="mt-4 grid gap-2 sm:grid-cols-4">
                    <MetricaPlano rotulo="Mensal" valor={formatarMoeda(plano.valorMensal)} />
                    <MetricaPlano rotulo="Usuários" valor={String(plano.limiteUsuarios)} />
                    <MetricaPlano rotulo="Clientes" valor={String(plano.limiteClientes)} />
                    <MetricaPlano rotulo="Profissionais" valor={String(plano.limiteProfissionais)} />
                  </div>
                </article>
              ))}
            </div>
          )}
        </div>
      </div>

      <aside className="rounded-lg border bg-card p-4 shadow-sm">
        <div className="mb-4 flex items-center justify-between gap-3 border-b pb-4">
          <div>
            <p className="text-sm font-medium text-primary">{planoEditando ? "Editar" : "Novo"}</p>
            <h2 className="mt-1 text-lg font-semibold text-card-foreground">Plano</h2>
          </div>
          <Button type="button" variant="outline" size="icon" onClick={limparFormulario} title="Novo plano">
            <Plus className="h-4 w-4" />
          </Button>
        </div>

        <form className="grid gap-3" onSubmit={salvar}>
          <CampoTexto id="codigo" label="Código" erro={form.formState.errors.codigo?.message} registro={form.register("codigo")} />
          <CampoTexto id="nome" label="Nome" erro={form.formState.errors.nome?.message} registro={form.register("nome")} />
          <CampoTexto id="descricao" label="Descrição" erro={form.formState.errors.descricao?.message} registro={form.register("descricao")} />

          <div className="grid grid-cols-2 gap-3">
            <CampoNumero id="valorMensal" label="Mensal" erro={form.formState.errors.valorMensal?.message} registro={form.register("valorMensal")} />
            <CampoNumero id="limiteUsuarios" label="Usuários" erro={form.formState.errors.limiteUsuarios?.message} registro={form.register("limiteUsuarios")} />
            <CampoNumero id="limiteClientes" label="Clientes" erro={form.formState.errors.limiteClientes?.message} registro={form.register("limiteClientes")} />
            <CampoNumero id="limiteProfissionais" label="Profissionais" erro={form.formState.errors.limiteProfissionais?.message} registro={form.register("limiteProfissionais")} />
          </div>

          <CampoTexto id="modulosTexto" label="Módulos" erro={form.formState.errors.modulosTexto?.message} registro={form.register("modulosTexto")} />
          <CampoTexto id="marcaDaguaAcademica" label="Marca d'água" erro={form.formState.errors.marcaDaguaAcademica?.message} registro={form.register("marcaDaguaAcademica")} />

          <div className="grid grid-cols-2 gap-3">
            <label className="flex items-center gap-2 rounded-md border bg-background px-3 py-2 text-sm font-medium text-card-foreground">
              <input type="checkbox" className="h-4 w-4 accent-primary" {...form.register("ativo")} />
              Ativo
            </label>
            <label className="flex items-center gap-2 rounded-md border bg-background px-3 py-2 text-sm font-medium text-card-foreground">
              <input type="checkbox" className="h-4 w-4 accent-primary" {...form.register("estudante")} />
              Estudante
            </label>
          </div>

          {erro ? <div className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{erro}</div> : null}

          <Button type="submit" disabled={salvarMutation.isPending}>
            {salvarMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <CheckCircle2 className="h-4 w-4" />}
            Salvar plano
          </Button>
        </form>
      </aside>
    </section>
  );
}

function MetricaPlano({ rotulo, valor }: { rotulo: string; valor: string }) {
  return (
    <div className="rounded-md border bg-card px-3 py-2">
      <p className="text-xs font-medium uppercase text-muted-foreground">{rotulo}</p>
      <p className="mt-1 text-sm font-semibold text-card-foreground">{valor}</p>
    </div>
  );
}

type CampoProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  erro?: string;
  registro: UseFormRegisterReturn;
};

function CampoTexto({ label, erro, registro, ...props }: CampoProps) {
  return (
    <label className="grid gap-1 text-sm font-medium text-card-foreground">
      {label}
      <input
        className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
        {...registro}
        {...props}
      />
      {erro ? <span className="text-xs text-red-600">{erro}</span> : null}
    </label>
  );
}

function CampoNumero(props: CampoProps) {
  return <CampoTexto type="number" min={0} step="0.01" {...props} />;
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(valor);
}
