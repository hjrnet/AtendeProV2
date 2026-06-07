package br.com.atendepro.modules.integracao.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.integracao.domain.model.TipoIntegracaoFutura;

class IntegracaoFuturaServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-07T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void deveConsultarStatusDoWhatsAppFuturoSemCredenciais() {
        var properties = new IntegracoesFuturasProperties(new IntegracoesFuturasProperties.WhatsApp(false, null, null));
        var service = new IntegracaoFuturaService(properties, CLOCK);

        var result = service.consultarStatusWhatsApp();

        assertThat(result.tipo()).isEqualTo(TipoIntegracaoFutura.WHATSAPP);
        assertThat(result.configurada()).isFalse();
        assertThat(result.provedor()).isEqualTo("oficial");
        assertThat(result.ambiente()).isEqualTo("sandbox");
        assertThat(result.proximasEtapas()).hasSize(3);
        assertThat(result.consultadoEm()).isEqualTo(Instant.parse("2026-06-07T12:00:00Z"));
    }

    @Test
    void deveConsultarStatusDoWhatsAppConfigurado() {
        var properties = new IntegracoesFuturasProperties(new IntegracoesFuturasProperties.WhatsApp(true, "meta", "producao"));
        var service = new IntegracaoFuturaService(properties, CLOCK);

        var result = service.consultarStatusWhatsApp();

        assertThat(result.configurada()).isTrue();
        assertThat(result.provedor()).isEqualTo("meta");
        assertThat(result.mensagem()).contains("ativacao operacional");
    }

    @Test
    void deveConsultarStatusDoPagamentosSemCredenciais() {
        var properties = new IntegracoesFuturasProperties(
                null,
                new IntegracoesFuturasProperties.Pagamentos(false, null, null),
                new IntegracoesFuturasProperties.AssinaturaDigital(false, null, null)
        );
        var service = new IntegracaoFuturaService(properties, CLOCK);

        var result = service.consultarStatusPagamentos();

        assertThat(result.tipo()).isEqualTo(TipoIntegracaoFutura.PAGAMENTOS);
        assertThat(result.configurada()).isFalse();
        assertThat(result.provedor()).isEqualTo("gateway-futuro");
        assertThat(result.ambiente()).isEqualTo("sandbox");
        assertThat(result.mensagem()).contains("gateway");
        assertThat(result.proximasEtapas()).hasSize(3);
        assertThat(result.consultadoEm()).isEqualTo(Instant.parse("2026-06-07T12:00:00Z"));
    }

    @Test
    void deveConsultarStatusDoPagamentosConfigurado() {
        var properties = new IntegracoesFuturasProperties(
                null,
                new IntegracoesFuturasProperties.Pagamentos(true, "pagar-me", "producao"),
                new IntegracoesFuturasProperties.AssinaturaDigital(false, null, null)
        );
        var service = new IntegracaoFuturaService(properties, CLOCK);

        var result = service.consultarStatusPagamentos();

        assertThat(result.configurada()).isTrue();
        assertThat(result.provedor()).isEqualTo("pagar-me");
        assertThat(result.ambiente()).isEqualTo("producao");
        assertThat(result.mensagem()).contains("operacional");
    }

    @Test
    void deveConsultarStatusDaAssinaturaDigitalSemCredenciais() {
        var properties = new IntegracoesFuturasProperties(
                null,
                new IntegracoesFuturasProperties.Pagamentos(false, null, null),
                new IntegracoesFuturasProperties.AssinaturaDigital(false, null, null)
        );
        var service = new IntegracaoFuturaService(properties, CLOCK);

        var result = service.consultarStatusAssinaturaDigital();

        assertThat(result.tipo()).isEqualTo(TipoIntegracaoFutura.ASSINATURA_DIGITAL);
        assertThat(result.configurada()).isFalse();
        assertThat(result.provedor()).isEqualTo("parceiro-futuro");
        assertThat(result.ambiente()).isEqualTo("sandbox");
        assertThat(result.proximasEtapas()).hasSize(3);
        assertThat(result.mensagem()).contains("assinatura digital");
    }

    @Test
    void deveConsultarStatusDaAssinaturaDigitalConfigurado() {
        var properties = new IntegracoesFuturasProperties(
                null,
                new IntegracoesFuturasProperties.Pagamentos(false, null, null),
                new IntegracoesFuturasProperties.AssinaturaDigital(true, "docsign", "homologacao")
        );
        var service = new IntegracaoFuturaService(properties, CLOCK);

        var result = service.consultarStatusAssinaturaDigital();

        assertThat(result.configurada()).isTrue();
        assertThat(result.provedor()).isEqualTo("docsign");
        assertThat(result.ambiente()).isEqualTo("homologacao");
        assertThat(result.mensagem()).contains("operacional");
    }
}
