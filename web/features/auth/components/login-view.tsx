"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { useRouter } from "next/navigation";
import {
  Activity,
  ArrowRight,
  BadgeCheck,
  KeyRound,
  LoaderCircle,
  Mail,
  ShieldCheck,
  UserRound
} from "lucide-react";

import { Button } from "@/components/ui/button";
import { ApiError } from "@/lib/api";
import { autenticarUsuario } from "@/features/auth/api/auth-client";
import { salvarSessaoAutenticada } from "@/features/auth/lib/auth-storage";
import { loginSchema, type LoginFormData } from "@/features/auth/lib/login-schema";

const CREDENCIAIS_DEMO: LoginFormData = {
  email: "admin@atendepro.local",
  senha: "AtendePro@2026"
};

const itensPainel = [
  { rotulo: "Nucleo", valor: "Auth e tenant", icone: ShieldCheck },
  { rotulo: "Ambiente", valor: "Local seguro", icone: Activity },
  { rotulo: "Release", valor: "R1", icone: BadgeCheck }
];

type LoginViewProps = {
  redirectTo?: string | string[];
};

export function LoginView({ redirectTo }: LoginViewProps) {
  const router = useRouter();
  const [erroGeral, setErroGeral] = useState<string | null>(null);
  const form = useForm<LoginFormData>({
    defaultValues: {
      email: "",
      senha: ""
    }
  });
  const destinoAposLogin = normalizarDestino(redirectTo);

  const entrar = form.handleSubmit(async (valores) => {
    setErroGeral(null);
    form.clearErrors();

    const validacao = loginSchema.safeParse(valores);
    if (!validacao.success) {
      validacao.error.issues.forEach((issue) => {
        const campo = issue.path.at(0);
        if (campo === "email" || campo === "senha") {
          form.setError(campo, { message: issue.message });
        }
      });
      return;
    }

    try {
      const response = await autenticarUsuario(validacao.data);
      salvarSessaoAutenticada(response);
      form.reset({ email: validacao.data.email, senha: "" });
      router.replace(destinoAposLogin);
    } catch (error) {
      if (error instanceof ApiError) {
        setErroGeral(error.message);
        return;
      }
      setErroGeral("Nao foi possivel entrar agora.");
    }
  });

  function preencherDemo() {
    form.setValue("email", CREDENCIAIS_DEMO.email, { shouldDirty: true });
    form.setValue("senha", CREDENCIAIS_DEMO.senha, { shouldDirty: true });
    form.clearErrors();
    setErroGeral(null);
  }

  return (
    <main className="min-h-screen px-4 py-5 sm:px-6 lg:px-10">
      <div className="mx-auto grid w-full max-w-6xl gap-5 lg:min-h-[calc(100vh-40px)] lg:grid-cols-[minmax(0,1fr)_420px] lg:items-center">
        <section className="flex flex-col gap-5">
          <div className="max-w-2xl">
            <p className="text-sm font-medium text-primary">AtendePro SaaS</p>
            <h1 className="mt-2 text-3xl font-semibold tracking-normal text-foreground sm:text-4xl">
              Acesso profissional para gestao multiempresa
            </h1>
            <p className="mt-3 max-w-xl text-base leading-7 text-muted-foreground">
              Auth, tenant e permissoes preparados para o nucleo operacional do AtendePro.
            </p>
          </div>

          <div className="grid gap-3 sm:grid-cols-3">
            {itensPainel.map((item) => {
              const Icone = item.icone;

              return (
                <article key={item.rotulo} className="rounded-lg border bg-card p-4 shadow-sm">
                  <div className="flex items-center gap-3">
                    <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 text-primary">
                      <Icone className="h-5 w-5" />
                    </span>
                    <div>
                      <p className="text-xs font-medium uppercase text-muted-foreground">{item.rotulo}</p>
                      <strong className="mt-1 block text-sm font-semibold text-card-foreground">{item.valor}</strong>
                    </div>
                  </div>
                </article>
              );
            })}
          </div>

        </section>

        <section className="rounded-lg border bg-card p-5 shadow-sm sm:p-6">
          <div className="mb-6 flex items-center justify-between gap-4">
            <div>
              <p className="text-sm font-medium text-muted-foreground">Entrar</p>
              <h2 className="mt-1 text-2xl font-semibold tracking-normal text-card-foreground">AtendePro</h2>
            </div>
            <span className="flex h-11 w-11 items-center justify-center rounded-md bg-primary text-primary-foreground">
              <KeyRound className="h-5 w-5" />
            </span>
          </div>

          <form className="flex flex-col gap-4" onSubmit={entrar} noValidate>
            <div className="flex flex-col gap-2">
              <label className="text-sm font-medium text-card-foreground" htmlFor="email">
                Email
              </label>
              <div className="relative">
                <Mail className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <input
                  id="email"
                  type="email"
                  autoComplete="email"
                  className="h-11 w-full rounded-md border bg-background pl-10 pr-3 text-sm outline-none transition-colors placeholder:text-muted-foreground focus:border-primary focus:ring-2 focus:ring-ring"
                  placeholder="voce@clinica.com"
                  {...form.register("email")}
                />
              </div>
              {form.formState.errors.email ? (
                <p className="text-sm text-red-600">{form.formState.errors.email.message}</p>
              ) : null}
            </div>

            <div className="flex flex-col gap-2">
              <label className="text-sm font-medium text-card-foreground" htmlFor="senha">
                Senha
              </label>
              <div className="relative">
                <ShieldCheck className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <input
                  id="senha"
                  type="password"
                  autoComplete="current-password"
                  className="h-11 w-full rounded-md border bg-background pl-10 pr-3 text-sm outline-none transition-colors placeholder:text-muted-foreground focus:border-primary focus:ring-2 focus:ring-ring"
                  placeholder="Senha"
                  {...form.register("senha")}
                />
              </div>
              {form.formState.errors.senha ? (
                <p className="text-sm text-red-600">{form.formState.errors.senha.message}</p>
              ) : null}
            </div>

            {erroGeral ? (
              <div className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{erroGeral}</div>
            ) : null}

            <div className="grid gap-3 sm:grid-cols-[1fr_auto]">
              <Button type="submit" disabled={form.formState.isSubmitting}>
                {form.formState.isSubmitting ? (
                  <LoaderCircle className="h-4 w-4 animate-spin" />
                ) : (
                  <ArrowRight className="h-4 w-4" />
                )}
                Entrar
              </Button>
              <Button type="button" variant="secondary" onClick={preencherDemo}>
                <UserRound className="h-4 w-4" />
                Demo
              </Button>
            </div>
          </form>
        </section>
      </div>
    </main>
  );
}

function normalizarDestino(redirectTo?: string | string[]) {
  const destino = Array.isArray(redirectTo) ? redirectTo[0] : redirectTo;

  if (destino && destino.startsWith("/") && !destino.startsWith("//")) {
    return destino;
  }

  return "/app";
}
