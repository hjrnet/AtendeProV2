package br.com.atendepro.modules.marketing.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.marketing.application.result.LeadMarketingResult;
import br.com.atendepro.modules.marketing.domain.model.AreaInteresseLead;
import br.com.atendepro.modules.marketing.domain.model.StatusLeadMarketing;
import br.com.atendepro.modules.marketing.domain.model.TamanhoOperacaoLead;

public record LeadMarketingResponse(
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

    public static LeadMarketingResponse de(LeadMarketingResult result) {
        return new LeadMarketingResponse(
                result.id(),
                result.nome(),
                result.email(),
                result.telefone(),
                result.areaInteresse(),
                result.tamanhoOperacao(),
                result.origem(),
                result.mensagem(),
                result.status(),
                result.criadoEm()
        );
    }
}
