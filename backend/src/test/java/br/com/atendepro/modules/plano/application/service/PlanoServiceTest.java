package br.com.atendepro.modules.plano.application.service;

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

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.plano.application.command.AtualizarPlanoCommand;
import br.com.atendepro.modules.plano.application.command.CriarPlanoCommand;
import br.com.atendepro.modules.plano.application.port.out.AtualizarPlanoPort;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorCodigoPort;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorIdPort;
import br.com.atendepro.modules.plano.application.port.out.ListarPlanosPort;
import br.com.atendepro.modules.plano.application.port.out.SalvarPlanoPort;
import br.com.atendepro.modules.plano.domain.model.ModuloPlano;
import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class PlanoServiceTest {

    private static final Instant AGORA = Instant.parse("2026-05-25T10:00:00Z");

    private final FakePlanoAdapter adapter = new FakePlanoAdapter();
    private final PlanoService service = new PlanoService(
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
    void deveCriarPlanoComModulosELimites() {
        definirSuperAdmin();

        var plano = service.criarPlano(comandoCriacao("START"));

        assertThat(plano.codigo()).isEqualTo("START");
        assertThat(plano.valorMensal()).isEqualByComparingTo("99.90");
        assertThat(plano.modulos()).containsExactlyInAnyOrder(ModuloPlano.CLIENTES, ModuloPlano.AGENDA);
        assertThat(adapter.carregarPlanoPorCodigo("START")).isPresent();
    }

    @Test
    void naoDeveCriarPlanoComCodigoDuplicado() {
        definirSuperAdmin();
        service.criarPlano(comandoCriacao("START"));

        assertThatThrownBy(() -> service.criarPlano(comandoCriacao("start")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Codigo de plano ja cadastrado.");
    }

    @Test
    void deveAtualizarPlanoExistente() {
        definirSuperAdmin();
        var plano = service.criarPlano(comandoCriacao("CARE"));

        var atualizado = service.atualizarPlano(new AtualizarPlanoCommand(
                plano.id(),
                "CARE_PLUS",
                "Care Plus",
                "Plano revisado",
                new BigDecimal("199.90"),
                5,
                500,
                4,
                true,
                Set.of(ModuloPlano.CLIENTES, ModuloPlano.AGENDA, ModuloPlano.PROCEDIMENTOS)
        ));

        assertThat(atualizado).isPresent();
        assertThat(atualizado.get().codigo()).isEqualTo("CARE_PLUS");
        assertThat(atualizado.get().modulos()).contains(ModuloPlano.PROCEDIMENTOS);
    }

    @Test
    void deveListarPlanosComPaginacao() {
        definirSuperAdmin();
        service.criarPlano(comandoCriacao("START"));

        var planos = service.listarPlanos(Paginacao.primeiraPagina(20), "sta", true);

        assertThat(planos.totalItens()).isEqualTo(1);
        assertThat(planos.itens()).extracting("codigo").containsExactly("START");
        assertThat(adapter.ultimaBusca).isEqualTo("sta");
        assertThat(adapter.ultimoAtivo).isTrue();
    }

    @Test
    void naoDeveCriarPlanoSemPermissaoAdminSaas() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.EMPRESA_ADMIN)
        ));

        assertThatThrownBy(() -> service.criarPlano(comandoCriacao("START")))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private CriarPlanoCommand comandoCriacao(String codigo) {
        return new CriarPlanoCommand(
                codigo,
                "Plano " + codigo,
                "Plano de teste",
                new BigDecimal("99.90"),
                2,
                100,
                2,
                true,
                Set.of(ModuloPlano.CLIENTES, ModuloPlano.AGENDA)
        );
    }

    private void definirSuperAdmin() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.SUPER_ADMIN)
        ));
    }

    private static class FakePlanoAdapter implements
            SalvarPlanoPort,
            AtualizarPlanoPort,
            CarregarPlanoPorIdPort,
            CarregarPlanoPorCodigoPort,
            ListarPlanosPort {

        private final LinkedHashMap<UUID, PlanoAssinatura> planos = new LinkedHashMap<>();
        private String ultimaBusca;
        private Boolean ultimoAtivo;

        @Override
        public void salvarPlano(PlanoAssinatura plano) {
            planos.put(plano.id(), plano);
        }

        @Override
        public void atualizarPlano(PlanoAssinatura plano) {
            planos.put(plano.id(), plano);
        }

        @Override
        public Optional<PlanoAssinatura> carregarPlanoPorId(UUID planoId) {
            return Optional.ofNullable(planos.get(planoId));
        }

        @Override
        public Optional<PlanoAssinatura> carregarPlanoPorCodigo(String codigo) {
            String codigoNormalizado = PlanoAssinatura.normalizarCodigo(codigo);
            return planos.values().stream()
                    .filter(plano -> plano.codigo().equals(codigoNormalizado))
                    .findFirst();
        }

        @Override
        public ResultadoPaginado<PlanoAssinatura> listarPlanos(Paginacao paginacao, String busca, Boolean ativo) {
            ultimaBusca = busca;
            ultimoAtivo = ativo;
            List<PlanoAssinatura> itens = planos.values().stream()
                    .filter(plano -> ativo == null || plano.ativo() == ativo)
                    .filter(plano -> busca == null || plano.codigo().toLowerCase().contains(busca.toLowerCase()))
                    .toList();
            return new ResultadoPaginado<>(itens, itens.size(), paginacao.pagina(), paginacao.tamanho());
        }
    }
}
