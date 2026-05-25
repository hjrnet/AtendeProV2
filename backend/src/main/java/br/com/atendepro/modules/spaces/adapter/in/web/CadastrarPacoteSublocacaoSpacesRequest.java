package br.com.atendepro.modules.spaces.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.command.CadastrarPacoteSublocacaoSpacesCommand;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastrarPacoteSublocacaoSpacesRequest(
        UUID empresaId,
        UUID recursoId,
        @NotBlank @Size(max = 160) String nome,
        @NotNull TipoPacoteSublocacaoSpaces tipo,
        @Size(max = 1000) String descricao,
        @NotNull @DecimalMin(value = "0.01") BigDecimal duracaoHoras,
        @NotNull @DecimalMin(value = "0.00") BigDecimal valorFixo,
        @NotNull @DecimalMin(value = "0.00") @DecimalMax(value = "100.00") BigDecimal percentualReceita
) {

    CadastrarPacoteSublocacaoSpacesCommand paraCommand() {
        return new CadastrarPacoteSublocacaoSpacesCommand(
                empresaId,
                recursoId,
                nome,
                tipo,
                descricao,
                duracaoHoras,
                valorFixo,
                percentualReceita
        );
    }
}
