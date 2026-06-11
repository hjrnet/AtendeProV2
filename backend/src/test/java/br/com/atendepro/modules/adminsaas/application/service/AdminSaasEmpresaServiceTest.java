package br.com.atendepro.modules.adminsaas.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.adminsaas.application.command.AlterarBloqueioEmpresaAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.application.command.RegistrarEventoAuditoriaAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.application.port.out.AtualizarBloqueioEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.CarregarEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.ContarUsuariosEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.ListarEmpresasAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.RegistrarEventoAuditoriaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasDetalheResult;
import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasResumoResult;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

class AdminSaasEmpresaServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("2ec11856-578d-48fb-91b3-e8dd46dd2ad9");
    private static final Instant AGORA = Instant.parse("2026-05-25T10:00:00Z");

    private final FakeEmpresaAdminSaasAdapter adapter = new FakeEmpresaAdminSaasAdapter();
    private final AdminSaasEmpresaService service = new AdminSaasEmpresaService(
            new PermissaoAcessoService(),
            adapter,
            adapter,
            adapter,
            adapter,
            adapter,
            Clock.fixed(AGORA, ZoneOffset.UTC)
    );

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveListarEmpresasParaAdminSaas() {
        definirSuperAdmin();

        var empresas = service.listarEmpresas(Paginacao.primeiraPagina(20), "clinica");

        assertThat(empresas.totalItens()).isEqualTo(1);
        assertThat(empresas.itens()).extracting(EmpresaAdminSaasResumoResult::nomeFantasia)
                .containsExactly("Clinica Modelo");
        assertThat(adapter.ultimaBusca).isEqualTo("clinica");
    }

    @Test
    void deveDetalharEmpresaParaAdminSaas() {
        definirSuperAdmin();

        var empresa = service.detalharEmpresa(EMPRESA_ID);

        assertThat(empresa).isPresent();
        assertThat(empresa.get().documento()).isEqualTo("12345678000190");
    }

    @Test
    void deveAlterarBloqueioDaEmpresa() {
        definirSuperAdmin();

        var empresa = service.alterarBloqueioEmpresa(new AlterarBloqueioEmpresaAdminSaasCommand(EMPRESA_ID, true));

        assertThat(empresa).isPresent();
        assertThat(empresa.get().ativo()).isFalse();
        assertThat(adapter.eventosRegistrados).hasSize(1);
        assertThat(adapter.eventosRegistrados.get(0).tipo()).isEqualTo("EMPRESA_BLOQUEIO_ALTERADO");
    }

    @Test
    void deveObservarEmpresaComStatusOperacional() {
        definirSuperAdmin();
        service.alterarBloqueioEmpresa(new AlterarBloqueioEmpresaAdminSaasCommand(EMPRESA_ID, true));

        var observacao = service.observarEmpresa(EMPRESA_ID);

        assertThat(observacao).isPresent();
        assertThat(observacao.get().statusOperacional()).isEqualTo("BLOQUEADA");
        assertThat(observacao.get().usuariosVinculados()).isEqualTo(2);
        assertThat(observacao.get().observadoEm()).isEqualTo(AGORA);
    }

    @Test
    void naoDeveListarEmpresasSemPermissaoAdminSaas() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.EMPRESA_ADMIN)
        ));

        assertThatThrownBy(() -> service.listarEmpresas(Paginacao.primeiraPagina(20), null))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private void definirSuperAdmin() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.SUPER_ADMIN)
        ));
    }

    private static class FakeEmpresaAdminSaasAdapter implements
            ListarEmpresasAdminSaasPort,
            CarregarEmpresaAdminSaasPort,
            AtualizarBloqueioEmpresaAdminSaasPort,
            ContarUsuariosEmpresaAdminSaasPort,
            RegistrarEventoAuditoriaAdminSaasPort {

        private boolean ativo = true;
        private String ultimaBusca;
        private final List<RegistrarEventoAuditoriaAdminSaasCommand> eventosRegistrados = new java.util.ArrayList<>();

        @Override
        public ResultadoPaginado<EmpresaAdminSaasResumoResult> listarEmpresas(Paginacao paginacao, String busca) {
            ultimaBusca = busca;
            return new ResultadoPaginado<>(
                    List.of(new EmpresaAdminSaasResumoResult(
                            EMPRESA_ID,
                            "Clinica Modelo",
                            "12345678000190",
                            "contato@clinica.local",
                            ativo,
                            AGORA
                    )),
                    1,
                    paginacao.pagina(),
                    paginacao.tamanho()
            );
        }

        @Override
        public Optional<EmpresaAdminSaasDetalheResult> carregarEmpresa(UUID empresaId) {
            if (!EMPRESA_ID.equals(empresaId)) {
                return Optional.empty();
            }
            return Optional.of(new EmpresaAdminSaasDetalheResult(
                    EMPRESA_ID,
                    "Clinica Modelo",
                    "Clinica Modelo LTDA",
                    "12345678000190",
                    "contato@clinica.local",
                    "11999990000",
                    ativo,
                    AGORA
            ));
        }

        @Override
        public Optional<EmpresaAdminSaasDetalheResult> atualizarBloqueioEmpresa(UUID empresaId, boolean bloqueada) {
            if (!EMPRESA_ID.equals(empresaId)) {
                return Optional.empty();
            }
            ativo = !bloqueada;
            return carregarEmpresa(empresaId);
        }

        @Override
        public long contarUsuariosVinculados(UUID empresaId) {
            return EMPRESA_ID.equals(empresaId) ? 2 : 0;
        }

        @Override
        public void registrarEvento(RegistrarEventoAuditoriaAdminSaasCommand command) {
            eventosRegistrados.add(command);
        }
    }
}
