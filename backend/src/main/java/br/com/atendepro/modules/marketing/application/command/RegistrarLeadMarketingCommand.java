package br.com.atendepro.modules.marketing.application.command;

import br.com.atendepro.modules.marketing.domain.model.AreaInteresseLead;
import br.com.atendepro.modules.marketing.domain.model.TamanhoOperacaoLead;

public record RegistrarLeadMarketingCommand(
        String nome,
        String email,
        String telefone,
        AreaInteresseLead areaInteresse,
        TamanhoOperacaoLead tamanhoOperacao,
        String origem,
        String mensagem
) {
}
