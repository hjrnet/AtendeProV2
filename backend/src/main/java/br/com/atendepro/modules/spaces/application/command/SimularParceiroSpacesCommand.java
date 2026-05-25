package br.com.atendepro.modules.spaces.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record SimularParceiroSpacesCommand(
        UUID empresaId,
        UUID pacoteId,
        int quantidadePacotesMes,
        int atendimentosMes,
        BigDecimal ticketMedio,
        BigDecimal custosOperacionaisParceiro
) {
}
