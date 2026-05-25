package br.com.atendepro.modules.marketing.application.port.in;

import br.com.atendepro.modules.marketing.application.command.RegistrarLeadMarketingCommand;
import br.com.atendepro.modules.marketing.application.result.LeadMarketingResult;

public interface RegistrarLeadMarketingUseCase {

    LeadMarketingResult registrarLead(RegistrarLeadMarketingCommand command);
}
