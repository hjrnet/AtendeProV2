package br.com.atendepro.modules.marketing.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.marketing.application.command.RegistrarLeadMarketingCommand;
import br.com.atendepro.modules.marketing.application.port.out.SalvarLeadMarketingPort;
import br.com.atendepro.modules.marketing.domain.model.AreaInteresseLead;
import br.com.atendepro.modules.marketing.domain.model.LeadMarketing;
import br.com.atendepro.modules.marketing.domain.model.StatusLeadMarketing;
import br.com.atendepro.modules.marketing.domain.model.TamanhoOperacaoLead;
import br.com.atendepro.shared.domain.exception.BusinessException;

class LeadMarketingServiceTest {

    private final FakeSalvarLeadMarketingPort salvarLeadMarketingPort = new FakeSalvarLeadMarketingPort();
    private final LeadMarketingService service = new LeadMarketingService(
            salvarLeadMarketingPort,
            Clock.fixed(Instant.parse("2026-05-25T12:00:00Z"), ZoneOffset.UTC)
    );

    @Test
    void deveRegistrarLeadMarketing() {
        var result = service.registrarLead(new RegistrarLeadMarketingCommand(
                "Karol Nutri",
                "KAROL@EXEMPLO.COM",
                "(21) 99999-0000",
                AreaInteresseLead.NUTRI_PRO,
                TamanhoOperacaoLead.PROFISSIONAL_SOLO,
                "calculadora-preco-ideal",
                "Quero testar a plataforma"
        ));

        assertThat(result.id()).isNotNull();
        assertThat(result.email()).isEqualTo("karol@exemplo.com");
        assertThat(result.areaInteresse()).isEqualTo(AreaInteresseLead.NUTRI_PRO);
        assertThat(result.status()).isEqualTo(StatusLeadMarketing.NOVO);
        assertThat(salvarLeadMarketingPort.ultimoLead).isNotNull();
        assertThat(salvarLeadMarketingPort.ultimoLead.criadoEm()).isEqualTo(Instant.parse("2026-05-25T12:00:00Z"));
    }

    @Test
    void deveValidarEmailObrigatorio() {
        assertThatThrownBy(() -> service.registrarLead(new RegistrarLeadMarketingCommand(
                "Lead Invalido",
                "email-invalido",
                null,
                AreaInteresseLead.BEAUTY_PRO,
                TamanhoOperacaoLead.CLINICA,
                "landing",
                null
        ))).isInstanceOf(BusinessException.class)
                .hasMessage("Email invalido.");
    }

    private static class FakeSalvarLeadMarketingPort implements SalvarLeadMarketingPort {

        private LeadMarketing ultimoLead;

        @Override
        public void salvarLead(LeadMarketing lead) {
            this.ultimoLead = lead;
        }
    }
}
