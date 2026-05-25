package br.com.atendepro.modules.spaces.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.spaces.application.result.IndicadoresSublocacaoSpacesResult;
import br.com.atendepro.modules.spaces.application.result.RelatorioSublocacaoSpacesResult;

class RelatorioSublocacaoSpacesServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("47a55821-b779-443a-b314-ad125bc89f71");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T12:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveConsultarIndicadoresDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        AtomicReference<UUID> empresaConsultada = new AtomicReference<>();
        RelatorioSublocacaoSpacesService service = new RelatorioSublocacaoSpacesService(
                (empresaId, periodoInicio, periodoFim) -> {
                    empresaConsultada.set(empresaId);
                    assertThat(periodoInicio).isEqualTo(Instant.parse("2026-05-01T03:00:00Z"));
                    assertThat(periodoFim).isEqualTo(Instant.parse("2026-06-01T03:00:00Z"));
                    return indicadores(empresaId, periodoInicio, periodoFim);
                },
                indicadores -> new RelatorioSublocacaoSpacesResult("spaces.pdf", "application/pdf", "%PDF".getBytes()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.consultarIndicadores(null);

        assertThat(empresaConsultada.get()).isEqualTo(EMPRESA_ID);
        assertThat(result.receitaFixaPrevistaMes()).isEqualByComparingTo("160.00");
        assertThat(result.taxaOcupacaoMesPercentual()).isEqualByComparingTo("4.55");
    }

    @Test
    void deveGerarRelatorioPdfComIndicadores() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        RelatorioSublocacaoSpacesService service = new RelatorioSublocacaoSpacesService(
                (empresaId, periodoInicio, periodoFim) -> indicadores(empresaId, periodoInicio, periodoFim),
                indicadores -> new RelatorioSublocacaoSpacesResult(
                        "spaces-sublocacao-202605.pdf",
                        "application/pdf",
                        "%PDF teste".getBytes()
                ),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.gerarRelatorio(null);

        assertThat(result.nomeArquivo()).isEqualTo("spaces-sublocacao-202605.pdf");
        assertThat(result.contentType()).isEqualTo("application/pdf");
        assertThat(new String(result.conteudo(), 0, 4)).isEqualTo("%PDF");
    }

    private IndicadoresSublocacaoSpacesResult indicadores(UUID empresaId, Instant periodoInicio, Instant periodoFim) {
        return new IndicadoresSublocacaoSpacesResult(
                empresaId,
                periodoInicio,
                periodoFim,
                3,
                2,
                4,
                1,
                1,
                0,
                new BigDecimal("16.00"),
                new BigDecimal("160.00"),
                new BigDecimal("4.55")
        );
    }
}
