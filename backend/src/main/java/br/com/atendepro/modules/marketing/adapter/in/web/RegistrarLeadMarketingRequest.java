package br.com.atendepro.modules.marketing.adapter.in.web;

import br.com.atendepro.modules.marketing.application.command.RegistrarLeadMarketingCommand;
import br.com.atendepro.modules.marketing.domain.model.AreaInteresseLead;
import br.com.atendepro.modules.marketing.domain.model.TamanhoOperacaoLead;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarLeadMarketingRequest(
        @NotBlank(message = "nome e obrigatorio")
        String nome,
        @NotBlank(message = "email e obrigatorio")
        @Email(message = "email invalido")
        String email,
        String telefone,
        @NotNull(message = "area de interesse e obrigatoria")
        AreaInteresseLead areaInteresse,
        @NotNull(message = "tamanho da operacao e obrigatorio")
        TamanhoOperacaoLead tamanhoOperacao,
        @NotBlank(message = "origem e obrigatoria")
        String origem,
        String mensagem
) {

    public RegistrarLeadMarketingCommand paraCommand() {
        return new RegistrarLeadMarketingCommand(
                nome,
                email,
                telefone,
                areaInteresse,
                tamanhoOperacao,
                origem,
                mensagem
        );
    }
}
