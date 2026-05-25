package br.com.atendepro.modules.auth.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PerfilAcessoTest {

    @Test
    void deveMapearPermissoesBaseDoSuperAdmin() {
        assertThat(PerfilAcesso.SUPER_ADMIN.permissoes())
                .contains(
                        PermissaoAcesso.CADASTRAR_EMPRESA,
                        PermissaoAcesso.CADASTRAR_ADMINISTRADOR_EMPRESA,
                        PermissaoAcesso.ACESSAR_ADMIN_SAAS,
                        PermissaoAcesso.ACESSAR_SUPORTE,
                        PermissaoAcesso.GERENCIAR_CHAMADOS
                );
    }

    @Test
    void deveMapearPermissoesRestritasDoProfissional() {
        assertThat(PerfilAcesso.PROFISSIONAL.permissoes())
                .containsExactlyInAnyOrder(
                        PermissaoAcesso.VISUALIZAR_EMPRESA,
                        PermissaoAcesso.GERENCIAR_CLIENTES,
                        PermissaoAcesso.GERENCIAR_AGENDA,
                        PermissaoAcesso.GERENCIAR_SERVICOS,
                        PermissaoAcesso.GERENCIAR_CUSTOS,
                        PermissaoAcesso.GERENCIAR_ESTOQUE,
                        PermissaoAcesso.GERENCIAR_EQUIPAMENTOS,
                        PermissaoAcesso.GERENCIAR_PRECIFICACAO,
                        PermissaoAcesso.GERENCIAR_SPACES,
                        PermissaoAcesso.GERENCIAR_DOCUMENTOS,
                        PermissaoAcesso.GERENCIAR_CHAMADOS,
                        PermissaoAcesso.ACESSAR_VERTICAIS
                );
        assertThat(PerfilAcesso.PROFISSIONAL.possuiPermissao(PermissaoAcesso.CADASTRAR_EMPRESA)).isFalse();
    }
}
