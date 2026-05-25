package br.com.atendepro.modules.auth.domain.model;

import java.util.Set;

public enum PerfilAcesso {
    SUPER_ADMIN(Set.of(
            PermissaoAcesso.CADASTRAR_EMPRESA,
            PermissaoAcesso.LISTAR_EMPRESAS,
            PermissaoAcesso.VISUALIZAR_EMPRESA,
            PermissaoAcesso.CADASTRAR_ADMINISTRADOR_EMPRESA,
            PermissaoAcesso.GERENCIAR_USUARIOS,
            PermissaoAcesso.GERENCIAR_CLIENTES,
            PermissaoAcesso.GERENCIAR_AGENDA,
            PermissaoAcesso.GERENCIAR_SERVICOS,
            PermissaoAcesso.GERENCIAR_CUSTOS,
            PermissaoAcesso.GERENCIAR_ESTOQUE,
            PermissaoAcesso.GERENCIAR_EQUIPAMENTOS,
            PermissaoAcesso.GERENCIAR_PRECIFICACAO,
            PermissaoAcesso.GERENCIAR_SPACES,
            PermissaoAcesso.GERENCIAR_DOCUMENTOS,
            PermissaoAcesso.ACESSAR_ADMIN_SAAS,
            PermissaoAcesso.ACESSAR_SUPORTE
    )),
    SUPORTE(Set.of(
            PermissaoAcesso.LISTAR_EMPRESAS,
            PermissaoAcesso.VISUALIZAR_EMPRESA,
            PermissaoAcesso.ACESSAR_ADMIN_SAAS,
            PermissaoAcesso.ACESSAR_SUPORTE
    )),
    EMPRESA_ADMIN(Set.of(
            PermissaoAcesso.LISTAR_EMPRESAS,
            PermissaoAcesso.VISUALIZAR_EMPRESA,
            PermissaoAcesso.CADASTRAR_ADMINISTRADOR_EMPRESA,
            PermissaoAcesso.GERENCIAR_USUARIOS,
            PermissaoAcesso.GERENCIAR_CLIENTES,
            PermissaoAcesso.GERENCIAR_AGENDA,
            PermissaoAcesso.GERENCIAR_SERVICOS,
            PermissaoAcesso.GERENCIAR_CUSTOS,
            PermissaoAcesso.GERENCIAR_ESTOQUE,
            PermissaoAcesso.GERENCIAR_EQUIPAMENTOS,
            PermissaoAcesso.GERENCIAR_PRECIFICACAO,
            PermissaoAcesso.GERENCIAR_SPACES,
            PermissaoAcesso.GERENCIAR_DOCUMENTOS
    )),
    PROFISSIONAL(Set.of(
            PermissaoAcesso.VISUALIZAR_EMPRESA,
            PermissaoAcesso.GERENCIAR_CLIENTES,
            PermissaoAcesso.GERENCIAR_AGENDA,
            PermissaoAcesso.GERENCIAR_SERVICOS,
            PermissaoAcesso.GERENCIAR_CUSTOS,
            PermissaoAcesso.GERENCIAR_ESTOQUE,
            PermissaoAcesso.GERENCIAR_EQUIPAMENTOS,
            PermissaoAcesso.GERENCIAR_PRECIFICACAO,
            PermissaoAcesso.GERENCIAR_SPACES,
            PermissaoAcesso.GERENCIAR_DOCUMENTOS
    )),
    RECEPCIONISTA(Set.of(
            PermissaoAcesso.VISUALIZAR_EMPRESA,
            PermissaoAcesso.GERENCIAR_CLIENTES,
            PermissaoAcesso.GERENCIAR_AGENDA,
            PermissaoAcesso.GERENCIAR_SERVICOS,
            PermissaoAcesso.GERENCIAR_CUSTOS,
            PermissaoAcesso.GERENCIAR_ESTOQUE,
            PermissaoAcesso.GERENCIAR_EQUIPAMENTOS,
            PermissaoAcesso.GERENCIAR_PRECIFICACAO,
            PermissaoAcesso.GERENCIAR_SPACES,
            PermissaoAcesso.GERENCIAR_DOCUMENTOS
    )),
    CLIENTE(Set.of()),
    ESTUDANTE(Set.of());

    private final Set<PermissaoAcesso> permissoes;

    PerfilAcesso(Set<PermissaoAcesso> permissoes) {
        this.permissoes = Set.copyOf(permissoes);
    }

    public Set<PermissaoAcesso> permissoes() {
        return permissoes;
    }

    public boolean possuiPermissao(PermissaoAcesso permissao) {
        return permissoes.contains(permissao);
    }
}
