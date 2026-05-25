package br.com.atendepro.modules.auth.domain.model;

public enum PermissaoAcesso {
    CADASTRAR_EMPRESA("empresa:cadastrar"),
    LISTAR_EMPRESAS("empresa:listar"),
    VISUALIZAR_EMPRESA("empresa:visualizar"),
    CADASTRAR_ADMINISTRADOR_EMPRESA("empresa:administrador:cadastrar"),
    GERENCIAR_USUARIOS("usuarios:gerenciar"),
    GERENCIAR_CLIENTES("clientes:gerenciar"),
    GERENCIAR_AGENDA("agenda:gerenciar"),
    GERENCIAR_SERVICOS("servicos:gerenciar"),
    ACESSAR_ADMIN_SAAS("admin-saas:acessar"),
    ACESSAR_SUPORTE("suporte:acessar");

    private final String authority;

    PermissaoAcesso(String authority) {
        this.authority = authority;
    }

    public String authority() {
        return authority;
    }
}
