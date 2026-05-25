package br.com.atendepro.modules.spaces.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.SimulacaoParceiroSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.StatusSimulacaoParceiroSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;

public record SimulacaoParceiroSpacesResponse(
        UUID empresaId,
        UUID pacoteId,
        String nomePacote,
        TipoPacoteSublocacaoSpaces tipoPacote,
        int quantidadePacotesMes,
        int atendimentosMes,
        BigDecimal ticketMedio,
        BigDecimal receitaBrutaMensal,
        BigDecimal custoFixoSublocacao,
        BigDecimal custoPercentualSublocacao,
        BigDecimal custoTotalSublocacao,
        BigDecimal custosOperacionaisParceiro,
        BigDecimal lucroEstimadoParceiro,
        BigDecimal margemParceiroPercentual,
        StatusSimulacaoParceiroSpaces status
) {

    static SimulacaoParceiroSpacesResponse de(SimulacaoParceiroSpacesResult result) {
        return new SimulacaoParceiroSpacesResponse(
                result.empresaId(),
                result.pacoteId(),
                result.nomePacote(),
                result.tipoPacote(),
                result.quantidadePacotesMes(),
                result.atendimentosMes(),
                result.ticketMedio(),
                result.receitaBrutaMensal(),
                result.custoFixoSublocacao(),
                result.custoPercentualSublocacao(),
                result.custoTotalSublocacao(),
                result.custosOperacionaisParceiro(),
                result.lucroEstimadoParceiro(),
                result.margemParceiroPercentual(),
                result.status()
        );
    }
}
