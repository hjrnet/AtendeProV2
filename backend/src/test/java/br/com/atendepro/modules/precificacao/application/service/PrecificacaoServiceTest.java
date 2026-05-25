package br.com.atendepro.modules.precificacao.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.precificacao.application.command.CalcularCustoRealCommand;
import br.com.atendepro.modules.precificacao.application.command.CalcularPrecificacaoBaseCommand;
import br.com.atendepro.modules.precificacao.application.command.ItemCustoPrecificacaoCommand;
import br.com.atendepro.modules.precificacao.application.port.out.CarregarServicoParaPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.result.ServicoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.domain.model.CategoriaItemPrecificacao;
import br.com.atendepro.shared.domain.exception.BusinessException;

class PrecificacaoServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("7b869806-f196-488c-bc11-937c4f5b2f38");
    private static final UUID SERVICO_ID = UUID.fromString("e59739a0-7a21-4720-8ac0-65ba4a2bb548");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCalcularPrecificacaoBaseNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> {
            assertThat(empresaId).isEqualTo(EMPRESA_ID);
            assertThat(servicoProcedimentoId).isEqualTo(SERVICO_ID);
            return Optional.of(new ServicoPrecificacaoResult(
                    SERVICO_ID,
                    EMPRESA_ID,
                    "Consulta Nutricional",
                    60,
                    new BigDecimal("250.00")
            ));
        });

        var result = service.calcularPrecificacaoBase(command(null, SERVICO_ID, null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.nomeProcedimento()).isEqualTo("Consulta Nutricional");
        assertThat(result.precoBaseServico()).isEqualByComparingTo("250.00");
        assertThat(result.custoTotal()).isEqualByComparingTo("90.00");
        assertThat(result.calculadoEm()).isEqualTo(Instant.parse("2026-05-25T00:00:00Z"));
    }

    @Test
    void deveExigirEmpresaParaUsuarioGlobal() {
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        assertThatThrownBy(() -> service.calcularPrecificacaoBase(command(null, null, "Consulta")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa e obrigatoria para operar precificacao.");
    }

    @Test
    void naoDeveOperarSemPermissaoDePrecificacao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        assertThatThrownBy(() -> service.calcularPrecificacaoBase(command(null, null, "Consulta")))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    @Test
    void naoDeveCalcularComServicoInexistente() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        assertThatThrownBy(() -> service.calcularPrecificacaoBase(command(null, SERVICO_ID, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Servico ou procedimento nao encontrado para precificacao.");
    }

    @Test
    void deveCalcularCustoRealUsandoDuracaoDoServico() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.of(new ServicoPrecificacaoResult(
                SERVICO_ID,
                EMPRESA_ID,
                "Consulta Nutricional",
                90,
                new BigDecimal("350.00")
        )));

        var result = service.calcularCustoReal(new CalcularCustoRealCommand(
                null,
                SERVICO_ID,
                null,
                null,
                new BigDecimal("45.00"),
                new BigDecimal("60.00"),
                new BigDecimal("120.00"),
                new BigDecimal("25.00"),
                new BigDecimal("15.00"),
                new BigDecimal("10.00")
        ));

        assertThat(result.nomeProcedimento()).isEqualTo("Consulta Nutricional");
        assertThat(result.duracaoMinutos()).isEqualTo(90);
        assertThat(result.custoSala()).isEqualByComparingTo("90.00");
        assertThat(result.custoTempoProfissional()).isEqualByComparingTo("180.00");
        assertThat(result.custoTotal()).isEqualByComparingTo("365.00");
    }

    @Test
    void deveExigirDuracaoParaCustoRealSemServico() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        assertThatThrownBy(() -> service.calcularCustoReal(new CalcularCustoRealCommand(
                null,
                null,
                "Consulta",
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        ))).isInstanceOf(BusinessException.class)
                .hasMessage("Duracao do procedimento e obrigatoria para calcular custo real.");
    }

    private PrecificacaoService service(CarregarServicoParaPrecificacaoPort carregarPort) {
        return new PrecificacaoService(
                carregarPort,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CalcularPrecificacaoBaseCommand command(UUID empresaId, UUID servicoId, String nomeProcedimento) {
        return new CalcularPrecificacaoBaseCommand(
                empresaId,
                servicoId,
                nomeProcedimento,
                List.of(
                        new ItemCustoPrecificacaoCommand(
                                "Insumos",
                                CategoriaItemPrecificacao.INSUMO,
                                new BigDecimal("40.00")
                        ),
                        new ItemCustoPrecificacaoCommand(
                                "Sala",
                                CategoriaItemPrecificacao.SALA,
                                new BigDecimal("50.00")
                        )
                )
        );
    }
}
