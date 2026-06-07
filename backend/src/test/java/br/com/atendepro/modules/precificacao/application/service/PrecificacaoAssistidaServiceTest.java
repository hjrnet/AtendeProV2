package br.com.atendepro.modules.precificacao.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.precificacao.domain.model.AnaliseMargemLucroPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.CustoRealPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.PrecoMinimoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.PrecoRecomendadoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;
import br.com.atendepro.shared.domain.exception.BusinessException;

class PrecificacaoAssistidaServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("7b869806-f196-488c-bc11-937c4f5b2f38");
    private static final UUID SIMULACAO_ID = UUID.fromString("105a876f-c749-4ad0-a646-174c9d856a48");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-07T10:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveGerarSugestoesParaPrecoEmPrejuizo() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        var service = service(simulacao(new BigDecimal("140.00")));

        var result = service.gerarSugestoes(SIMULACAO_ID);

        assertThat(result.statusMargem()).isEqualTo(StatusMargemPrecificacao.PREJUIZO);
        assertThat(result.resumo()).contains("prejuizo");
        assertThat(result.sugestoes()).extracting("tipo")
                .contains("AJUSTE_PRECO", "REVISAO_CUSTOS");
        assertThat(result.geradoEm()).isEqualTo(Instant.parse("2026-06-07T10:00:00Z"));
    }

    @Test
    void deveGerarSugestaoComercialParaPrecoSaudavel() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        var service = service(simulacao(new BigDecimal("260.00")));

        var result = service.gerarSugestoes(SIMULACAO_ID);

        assertThat(result.statusMargem()).isEqualTo(StatusMargemPrecificacao.SAUDAVEL);
        assertThat(result.sugestoes()).extracting("tipo").containsExactly("COMERCIAL");
    }

    @Test
    void deveFalharQuandoSimulacaoNaoExiste() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        var service = new PrecificacaoAssistidaService(
                id -> Optional.empty(),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        assertThatThrownBy(() -> service.gerarSugestoes(SIMULACAO_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Simulacao de precificacao nao encontrada.");
    }

    private PrecificacaoAssistidaService service(SimulacaoPrecificacao simulacao) {
        return new PrecificacaoAssistidaService(
                id -> Optional.of(simulacao),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private SimulacaoPrecificacao simulacao(BigDecimal precoVenda) {
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                EMPRESA_ID,
                null,
                "Consulta assistida",
                60,
                new BigDecimal("168.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.parse("2026-06-07T09:00:00Z")
        );
        PrecoMinimoPrecificacao precoMinimo = PrecoMinimoPrecificacao.calcular(custoReal);
        PrecoRecomendadoPrecificacao precoRecomendado = PrecoRecomendadoPrecificacao.calcular(
                precoMinimo,
                new BigDecimal("30.00")
        );
        AnaliseMargemLucroPrecificacao analise = AnaliseMargemLucroPrecificacao.analisar(precoMinimo, precoVenda);
        return new SimulacaoPrecificacao(
                SIMULACAO_ID,
                EMPRESA_ID,
                null,
                "Consulta assistida",
                60,
                new BigDecimal("168.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("30.00"),
                precoVenda,
                custoReal.custoTotal(),
                precoMinimo.precoMinimo(),
                precoRecomendado.precoRecomendado(),
                analise.lucroEstimado(),
                analise.margemRealPercentual(),
                analise.status(),
                true,
                Instant.parse("2026-06-07T09:00:00Z"),
                Instant.parse("2026-06-07T09:00:00Z")
        );
    }
}
