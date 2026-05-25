package br.com.atendepro.modules.beauty.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.beauty.application.command.ConsultarVisaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;
import br.com.atendepro.modules.beauty.application.result.MetricasBeautyProResult;
import br.com.atendepro.modules.beauty.domain.model.StatusOperacionalBeautyPro;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.domain.exception.BusinessException;

class BeautyProServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("a1ef4ac8-b9e3-4c52-934c-4cf0476a8d56");
    private static final UUID OUTRA_EMPRESA_ID = UUID.fromString("bd0c1e57-1341-46a3-a5df-5e85c0b5f2ba");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T10:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveConsultarVisaoBeautyProNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        BeautyProService service = service(metricasComDados());

        var result = service.consultarVisaoBeautyPro(new ConsultarVisaoBeautyProCommand(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.statusOperacional()).isEqualTo(StatusOperacionalBeautyPro.OPERACIONAL);
        assertThat(result.indicadores()).extracting("codigo")
                .contains("clientes", "agendaHoje", "servicos", "produtos", "precificacao", "alertas");
        assertThat(result.atalhosPrioritarios()).extracting("codigo")
                .containsExactly("ficha-estetica", "protocolos", "termos");
        assertThat(result.clientesRecentes()).extracting("nome").containsExactly("Juliana Beauty");
    }

    @Test
    void deveExigirEmpresaParaUsuarioGlobal() {
        BeautyProService service = service(metricasComDados());

        assertThatThrownBy(() -> service.consultarVisaoBeautyPro(new ConsultarVisaoBeautyProCommand(null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa e obrigatoria para operar Beauty Pro.");
    }

    @Test
    void naoDevePermitirOutraEmpresaParaTenantRestrito() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        BeautyProService service = service(metricasComDados());

        assertThatThrownBy(() -> service.consultarVisaoBeautyPro(new ConsultarVisaoBeautyProCommand(OUTRA_EMPRESA_ID)))
                .hasMessage("Usuario nao possui acesso a esta empresa.");
    }

    @Test
    void naoDeveConsultarBeautyProSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        BeautyProService service = service(metricasComDados());

        assertThatThrownBy(() -> service.consultarVisaoBeautyPro(new ConsultarVisaoBeautyProCommand(null)))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private BeautyProService service(MetricasBeautyProResult metricas) {
        return new BeautyProService(
                (empresaId, hoje) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(hoje).isEqualTo(LocalDate.parse("2026-05-25"));
                    return metricas;
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private MetricasBeautyProResult metricasComDados() {
        return new MetricasBeautyProResult(
                "Studio Aesthetic Premium",
                5,
                2,
                4,
                18,
                6,
                2,
                5,
                1,
                List.of(new ClienteBeautyResumoResult(
                        UUID.fromString("0183a2a8-bf74-4e4b-b1f1-532876a422d1"),
                        "Juliana Beauty",
                        "21999990000",
                        "Protocolo facial em acompanhamento.",
                        true,
                        Instant.parse("2026-05-25T09:00:00Z")
                ))
        );
    }
}
