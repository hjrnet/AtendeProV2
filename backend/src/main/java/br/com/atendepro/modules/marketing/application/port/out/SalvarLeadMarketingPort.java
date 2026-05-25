package br.com.atendepro.modules.marketing.application.port.out;

import br.com.atendepro.modules.marketing.domain.model.LeadMarketing;

public interface SalvarLeadMarketingPort {

    void salvarLead(LeadMarketing lead);
}
