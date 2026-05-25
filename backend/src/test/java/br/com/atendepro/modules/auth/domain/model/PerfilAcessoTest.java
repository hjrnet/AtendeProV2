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
                        PermissaoAcesso.ACESSAR_SUPORTE
                );
    }

    @Test
    void deveMapearPermissoesRestritasDoProfissional() {
        assertThat(PerfilAcesso.PROFISSIONAL.permissoes())
                .containsExactly(PermissaoAcesso.VISUALIZAR_EMPRESA);
        assertThat(PerfilAcesso.PROFISSIONAL.possuiPermissao(PermissaoAcesso.CADASTRAR_EMPRESA)).isFalse();
    }
}
