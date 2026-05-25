package br.com.atendepro.modules.empresa.application.context;

import java.util.Optional;

public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXTO = new ThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void definir(TenantContext contexto) {
        CONTEXTO.set(contexto);
    }

    public static Optional<TenantContext> contextoAtual() {
        return Optional.ofNullable(CONTEXTO.get());
    }

    public static void limpar() {
        CONTEXTO.remove();
    }
}
