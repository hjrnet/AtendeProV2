package br.com.atendepro.modules.mobile.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.domain.exception.AutenticacaoException;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;
import br.com.atendepro.modules.empresa.domain.model.DocumentoEmpresa;
import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;
import br.com.atendepro.modules.mobile.application.result.ClienteVinculadoMobileResult;
import br.com.atendepro.shared.domain.exception.BusinessException;

class MobilePerfilServiceTest {

    private static final UUID USUARIO_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID EMPRESA_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CLIENTE_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final Instant AGORA = Instant.parse("2026-06-11T09:00:00Z");

    @Test
    void deveConsultarPerfilMobileComClienteVinculadoPorEmail() {
        MobilePerfilService service = new MobilePerfilService(
                usuarioId -> Optional.of(usuario(Set.of(PerfilAcesso.CLIENTE), true, EMPRESA_ID)),
                empresaId -> Optional.of(empresa(true)),
                (empresaId, email) -> List.of(cliente(email))
        );

        var result = service.consultarPerfilMobile(USUARIO_ID);

        assertThat(result.usuarioId()).isEqualTo(USUARIO_ID);
        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.papelPrincipal()).isEqualTo("CLIENTE");
        assertThat(result.exigeVinculoCliente()).isTrue();
        assertThat(result.clientesVinculados()).extracting("id").containsExactly(CLIENTE_ID);
    }

    @Test
    void deveBloquearUsuarioInativo() {
        MobilePerfilService service = new MobilePerfilService(
                usuarioId -> Optional.of(usuario(Set.of(PerfilAcesso.CLIENTE), false, EMPRESA_ID)),
                empresaId -> Optional.of(empresa(true)),
                (empresaId, email) -> List.of()
        );

        assertThatThrownBy(() -> service.consultarPerfilMobile(USUARIO_ID))
                .isInstanceOf(AutenticacaoException.class)
                .hasMessageContaining("inativo");
    }

    @Test
    void deveBloquearUsuarioSemEmpresa() {
        MobilePerfilService service = new MobilePerfilService(
                usuarioId -> Optional.of(usuario(Set.of(PerfilAcesso.SUPER_ADMIN), true, null)),
                empresaId -> Optional.of(empresa(true)),
                (empresaId, email) -> List.of()
        );

        assertThatThrownBy(() -> service.consultarPerfilMobile(USUARIO_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("empresa");
    }

    @Test
    void deveBloquearEmpresaInativa() {
        MobilePerfilService service = new MobilePerfilService(
                usuarioId -> Optional.of(usuario(Set.of(PerfilAcesso.PROFISSIONAL), true, EMPRESA_ID)),
                empresaId -> Optional.of(empresa(false)),
                (empresaId, email) -> List.of()
        );

        assertThatThrownBy(() -> service.consultarPerfilMobile(USUARIO_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("bloqueada");
    }

    private UsuarioAutenticacao usuario(Set<PerfilAcesso> perfis, boolean ativo, UUID empresaId) {
        return new UsuarioAutenticacao(
                USUARIO_ID,
                empresaId,
                EmailUsuario.de("paciente@atendepro.local"),
                "Paciente Mobile",
                "$2a$10$hash",
                perfis,
                ativo,
                AGORA
        );
    }

    private EmpresaTenant empresa(boolean ativa) {
        return new EmpresaTenant(
                EMPRESA_ID,
                "Clinica Mobile",
                "Clinica Mobile LTDA",
                DocumentoEmpresa.de("12345678000190"),
                "contato@clinicamobile.local",
                "11999999999",
                ativa,
                AGORA
        );
    }

    private ClienteVinculadoMobileResult cliente(String email) {
        return new ClienteVinculadoMobileResult(
                CLIENTE_ID,
                EMPRESA_ID,
                "Paciente Mobile",
                "PACIENTE",
                "NUTRI",
                null,
                email,
                "11999999999",
                null,
                "Vinculo por email",
                true,
                AGORA,
                AGORA
        );
    }
}
