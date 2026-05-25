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
import br.com.atendepro.modules.assinatura.application.command.IniciarTrialCommand;
import br.com.atendepro.modules.assinatura.application.port.out.AtualizarTrialPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarTrialAtivoPorEmpresaPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarTrialPorIdPort;
import br.com.atendepro.modules.assinatura.application.port.out.ListarTrialsPort;
import br.com.atendepro.modules.assinatura.application.port.out.SalvarTrialPort;
import br.com.atendepro.modules.assinatura.domain.model.TrialAssinatura;
import br.com.atendepro.modules.assinatura.domain.model.TrialStatus;
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

class TrialServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("1ed27ee4-e787-46ef-8959-ecf52d94a807");
    private static final UUID PLANO_ID = UUID.fromString("ea7879ab-1d56-4121-95cf-28669869ce68");
    private static final Instant AGORA = Instant.parse("2026-05-25T10:00:00Z");

    private final FakeTrialAdapter trialAdapter = new FakeTrialAdapter();
    private final TrialService service = new TrialService(
            new PermissaoAcessoService(),
            new FakeEmpresaPort(),
            new FakePlanoPort(),
            trialAdapter,
            trialAdapter,
            trialAdapter,
            trialAdapter,
            trialAdapter,
            Clock.fixed(AGORA, ZoneOffset.UTC)
    );

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveIniciarTrialDeTrintaDias() {
        definirSuperAdmin();

        var trial = service.iniciarTrial(new IniciarTrialCommand(EMPRESA_ID, PLANO_ID));

        assertThat(trial.status()).isEqualTo(TrialStatus.ATIVO);
        assertThat(trial.diasRestantes()).isEqualTo(30);
        assertThat(trial.expiraEm()).isEqualTo(AGORA.plusSeconds(30L * 24 * 60 * 60));
    }

    @Test
    void naoDeveIniciarNovoTrialComTrialAtivoNaEmpresa() {
        definirSuperAdmin();
        service.iniciarTrial(new IniciarTrialCommand(EMPRESA_ID, PLANO_ID));

        assertThatThrownBy(() -> service.iniciarTrial(new IniciarTrialCommand(EMPRESA_ID, PLANO_ID)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa ja possui trial ativo.");
    }

    @Test
    void deveConverterTrialAtivo() {
        definirSuperAdmin();
        var trial = service.iniciarTrial(new IniciarTrialCommand(EMPRESA_ID, PLANO_ID));

        var convertido = service.converterTrial(trial.id());

        assertThat(convertido).isPresent();
        assertThat(convertido.get().status()).isEqualTo(TrialStatus.CONVERTIDO);
        assertThat(convertido.get().convertidoEm()).isEqualTo(AGORA);
    }

    @Test
    void deveListarTrials() {
        definirSuperAdmin();
        service.iniciarTrial(new IniciarTrialCommand(EMPRESA_ID, PLANO_ID));

        var trials = service.listarTrials(Paginacao.primeiraPagina(20), TrialStatus.ATIVO);

        assertThat(trials.totalItens()).isEqualTo(1);
        assertThat(trials.itens()).extracting("status").containsExactly(TrialStatus.ATIVO);
    }

    @Test
    void naoDeveIniciarTrialSemPermissaoAdminSaas() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.EMPRESA_ADMIN)
        ));

        assertThatThrownBy(() -> service.iniciarTrial(new IniciarTrialCommand(EMPRESA_ID, PLANO_ID)))
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
                    true,
                    AGORA
            ));
        }
    }

    private static class FakePlanoPort implements CarregarPlanoPorIdPort {

        @Override
        public Optional<PlanoAssinatura> carregarPlanoPorId(UUID planoId) {
            if (!PLANO_ID.equals(planoId)) {
                return Optional.empty();
            }
            return Optional.of(new PlanoAssinatura(
                    PLANO_ID,
                    "START",
                    "Start",
                    "Plano inicial",
                    new BigDecimal("79.90"),
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

    private static class FakeTrialAdapter implements
            SalvarTrialPort,
            AtualizarTrialPort,
            CarregarTrialAtivoPorEmpresaPort,
            CarregarTrialPorIdPort,
            ListarTrialsPort {

        private final LinkedHashMap<UUID, TrialAssinatura> trials = new LinkedHashMap<>();

        @Override
        public void salvarTrial(TrialAssinatura trial) {
            trials.put(trial.id(), trial);
        }

        @Override
        public void atualizarTrial(TrialAssinatura trial) {
            trials.put(trial.id(), trial);
        }

        @Override
        public Optional<TrialAssinatura> carregarTrialAtivoPorEmpresa(UUID empresaId) {
            return trials.values().stream()
                    .filter(trial -> trial.empresaId().equals(empresaId))
                    .filter(trial -> trial.status() == TrialStatus.ATIVO)
                    .findFirst();
        }

        @Override
        public Optional<TrialAssinatura> carregarTrialPorId(UUID trialId) {
            return Optional.ofNullable(trials.get(trialId));
        }

        @Override
        public ResultadoPaginado<TrialAssinatura> listarTrials(Paginacao paginacao, TrialStatus status) {
            List<TrialAssinatura> itens = trials.values().stream()
                    .filter(trial -> status == null || trial.status() == status)
                    .toList();
            return new ResultadoPaginado<>(itens, itens.size(), paginacao.pagina(), paginacao.tamanho());
        }
    }
}
