package br.com.atendepro.modules.assinatura.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.adminsaas.application.port.out.CarregarEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasDetalheResult;
import br.com.atendepro.modules.assinatura.application.command.AlterarPlanoAssinaturaCommand;
import br.com.atendepro.modules.assinatura.application.command.CriarAssinaturaCommand;
import br.com.atendepro.modules.assinatura.application.port.out.AtualizarAssinaturaPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarAssinaturaAtivaPorEmpresaPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarAssinaturaPorIdPort;
import br.com.atendepro.modules.assinatura.application.port.out.ListarAssinaturasPort;
import br.com.atendepro.modules.assinatura.application.port.out.SalvarAssinaturaPort;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaSaas;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaStatus;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorIdPort;
import br.com.atendepro.modules.plano.domain.model.ModuloPlano;
import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class AssinaturaServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("4156989d-3290-4a4d-a83b-3a72bdf008c3");
    private static final UUID PLANO_START_ID = UUID.fromString("ed7d5385-bd83-4d0e-82a4-f018c929ed7d");
    private static final UUID PLANO_CARE_ID = UUID.fromString("7aa2a7f1-ddb9-4c53-89dd-b38d4934fe7a");
    private static final Instant AGORA = Instant.parse("2026-05-25T10:00:00Z");

    private final FakeAssinaturaAdapter assinaturaAdapter = new FakeAssinaturaAdapter();
    private final AssinaturaService service = new AssinaturaService(
            new PermissaoAcessoService(),
            new FakeEmpresaPort(),
            new FakePlanoPort(),
            assinaturaAdapter,
            assinaturaAdapter,
            assinaturaAdapter,
            assinaturaAdapter,
            assinaturaAdapter,
            Clock.fixed(AGORA, ZoneOffset.UTC)
    );

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCriarAssinaturaAtiva() {
        definirSuperAdmin();

        var assinatura = service.criarAssinatura(new CriarAssinaturaCommand(EMPRESA_ID, PLANO_START_ID));

        assertThat(assinatura.status()).isEqualTo(AssinaturaStatus.ATIVA);
        assertThat(assinatura.planoId()).isEqualTo(PLANO_START_ID);
    }

    @Test
    void naoDeveCriarAssinaturaDuplicadaParaEmpresa() {
        definirSuperAdmin();
        service.criarAssinatura(new CriarAssinaturaCommand(EMPRESA_ID, PLANO_START_ID));

        assertThatThrownBy(() -> service.criarAssinatura(new CriarAssinaturaCommand(EMPRESA_ID, PLANO_CARE_ID)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa ja possui assinatura ativa.");
    }

    @Test
    void deveFazerUpgradeOuDowngradeAlterandoPlano() {
        definirSuperAdmin();
        var assinatura = service.criarAssinatura(new CriarAssinaturaCommand(EMPRESA_ID, PLANO_START_ID));

        var alterada = service.alterarPlanoAssinatura(
                new AlterarPlanoAssinaturaCommand(assinatura.id(), PLANO_CARE_ID)
        );

        assertThat(alterada).isPresent();
        assertThat(alterada.get().planoId()).isEqualTo(PLANO_CARE_ID);
    }

    @Test
    void deveBloquearDesbloquearECancelarAssinatura() {
        definirSuperAdmin();
        var assinatura = service.criarAssinatura(new CriarAssinaturaCommand(EMPRESA_ID, PLANO_START_ID));

        var bloqueada = service.bloquearAssinatura(assinatura.id());
        var desbloqueada = service.desbloquearAssinatura(assinatura.id());
        var cancelada = service.cancelarAssinatura(assinatura.id());

        assertThat(bloqueada.get().status()).isEqualTo(AssinaturaStatus.BLOQUEADA);
        assertThat(desbloqueada.get().status()).isEqualTo(AssinaturaStatus.ATIVA);
        assertThat(cancelada.get().status()).isEqualTo(AssinaturaStatus.CANCELADA);
    }

    @Test
    void deveListarAssinaturasPorStatus() {
        definirSuperAdmin();
        service.criarAssinatura(new CriarAssinaturaCommand(EMPRESA_ID, PLANO_START_ID));

        var assinaturas = service.listarAssinaturas(Paginacao.primeiraPagina(20), AssinaturaStatus.ATIVA);

        assertThat(assinaturas.totalItens()).isEqualTo(1);
        assertThat(assinaturas.itens()).extracting("status").containsExactly(AssinaturaStatus.ATIVA);
    }

    @Test
    void naoDeveCriarAssinaturaSemPermissaoAdminSaas() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.EMPRESA_ADMIN)
        ));

        assertThatThrownBy(() -> service.criarAssinatura(new CriarAssinaturaCommand(EMPRESA_ID, PLANO_START_ID)))
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

    private static class FakeEmpresaPort implements CarregarEmpresaAdminSaasPort {

        @Override
        public Optional<EmpresaAdminSaasDetalheResult> carregarEmpresa(UUID empresaId) {
            return EMPRESA_ID.equals(empresaId)
                    ? Optional.of(new EmpresaAdminSaasDetalheResult(
                            EMPRESA_ID,
                            "Clinica Modelo",
                            "Clinica Modelo LTDA",
                            "12345678000190",
                            "contato@clinica.local",
                            "11999990000",
                            true,
                            AGORA
                    ))
                    : Optional.empty();
        }
    }

    private static class FakePlanoPort implements CarregarPlanoPorIdPort {

        @Override
        public Optional<PlanoAssinatura> carregarPlanoPorId(UUID planoId) {
            if (!Set.of(PLANO_START_ID, PLANO_CARE_ID).contains(planoId)) {
                return Optional.empty();
            }
            return Optional.of(new PlanoAssinatura(
                    planoId,
                    planoId.equals(PLANO_START_ID) ? "START" : "CARE",
                    planoId.equals(PLANO_START_ID) ? "Start" : "Care",
                    "Plano",
                    planoId.equals(PLANO_START_ID) ? new BigDecimal("79.90") : new BigDecimal("129.90"),
                    2,
                    100,
                    1,
                    true,
                    false,
                    null,
                    Set.of(ModuloPlano.CLIENTES, ModuloPlano.AGENDA),
                    AGORA,
                    AGORA
            ));
        }
    }

    private static class FakeAssinaturaAdapter implements
            SalvarAssinaturaPort,
            AtualizarAssinaturaPort,
            CarregarAssinaturaAtivaPorEmpresaPort,
            CarregarAssinaturaPorIdPort,
            ListarAssinaturasPort {

        private final LinkedHashMap<UUID, AssinaturaSaas> assinaturas = new LinkedHashMap<>();

        @Override
        public void salvarAssinatura(AssinaturaSaas assinatura) {
            assinaturas.put(assinatura.id(), assinatura);
        }

        @Override
        public void atualizarAssinatura(AssinaturaSaas assinatura) {
            assinaturas.put(assinatura.id(), assinatura);
        }

        @Override
        public Optional<AssinaturaSaas> carregarAssinaturaAtivaPorEmpresa(UUID empresaId) {
            return assinaturas.values().stream()
                    .filter(assinatura -> assinatura.empresaId().equals(empresaId))
                    .filter(assinatura -> assinatura.status() == AssinaturaStatus.ATIVA
                            || assinatura.status() == AssinaturaStatus.BLOQUEADA)
                    .findFirst();
        }

        @Override
        public Optional<AssinaturaSaas> carregarAssinaturaPorId(UUID assinaturaId) {
            return Optional.ofNullable(assinaturas.get(assinaturaId));
        }

        @Override
        public ResultadoPaginado<AssinaturaSaas> listarAssinaturas(Paginacao paginacao, AssinaturaStatus status) {
            List<AssinaturaSaas> itens = assinaturas.values().stream()
                    .filter(assinatura -> status == null || assinatura.status() == status)
                    .toList();
            return new ResultadoPaginado<>(itens, itens.size(), paginacao.pagina(), paginacao.tamanho());
        }
    }
}
