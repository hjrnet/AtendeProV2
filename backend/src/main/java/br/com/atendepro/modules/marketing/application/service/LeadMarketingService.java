package br.com.atendepro.modules.marketing.application.service;

import java.time.Clock;
import java.time.Instant;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.marketing.application.command.RegistrarLeadMarketingCommand;
import br.com.atendepro.modules.marketing.application.port.in.RegistrarLeadMarketingUseCase;
import br.com.atendepro.modules.marketing.application.port.out.SalvarLeadMarketingPort;
import br.com.atendepro.modules.marketing.application.result.LeadMarketingResult;
import br.com.atendepro.modules.marketing.domain.model.LeadMarketing;

@Service
@Profile("!test")
public class LeadMarketingService implements RegistrarLeadMarketingUseCase {

    private final SalvarLeadMarketingPort salvarLeadMarketingPort;
    private final Clock clock;

    public LeadMarketingService(SalvarLeadMarketingPort salvarLeadMarketingPort, Clock clock) {
        this.salvarLeadMarketingPort = salvarLeadMarketingPort;
        this.clock = clock;
    }

    @Override
    public LeadMarketingResult registrarLead(RegistrarLeadMarketingCommand command) {
        LeadMarketing lead = LeadMarketing.registrar(
                command.nome(),
                command.email(),
                command.telefone(),
                command.areaInteresse(),
                command.tamanhoOperacao(),
                command.origem(),
                command.mensagem(),
                Instant.now(clock)
        );
        salvarLeadMarketingPort.salvarLead(lead);
        return LeadMarketingResult.de(lead);
    }
}
