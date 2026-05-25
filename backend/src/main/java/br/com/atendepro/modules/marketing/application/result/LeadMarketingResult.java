package br.com.atendepro.modules.marketing.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.marketing.domain.model.AreaInteresseLead;
import br.com.atendepro.modules.marketing.domain.model.LeadMarketing;
import br.com.atendepro.modules.marketing.domain.model.StatusLeadMarketing;
import br.com.atendepro.modules.marketing.domain.model.TamanhoOperacaoLead;

public record LeadMarketingResult(
        UUID id,
        String nome,
        String email,
        String telefone,
        AreaInteresseLead areaInteresse,
        TamanhoOperacaoLead tamanhoOperacao,
        String origem,
        String mensagem,
        StatusLeadMarketing status,
        Instant criadoEm
) {

    public static LeadMarketingResult de(LeadMarketing lead) {
        return new LeadMarketingResult(
                lead.id(),
                lead.nome(),
                lead.email(),
                lead.telefone(),
                lead.areaInteresse(),
                lead.tamanhoOperacao(),
                lead.origem(),
                lead.mensagem(),
                lead.status(),
                lead.criadoEm()
        );
    }
}
