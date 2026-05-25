package br.com.atendepro.modules.spaces.application.result;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.SimulacaoParceiroSpaces;
import br.com.atendepro.modules.spaces.domain.model.StatusSimulacaoParceiroSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;

public record SimulacaoParceiroSpacesResult(
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

    public static SimulacaoParceiroSpacesResult de(UUID empresaId, SimulacaoParceiroSpaces simulacao) {
        return new SimulacaoParceiroSpacesResult(
                empresaId,
                simulacao.pacoteId(),
                simulacao.nomePacote(),
                simulacao.tipoPacote(),
                simulacao.quantidadePacotesMes(),
                simulacao.atendimentosMes(),
                simulacao.ticketMedio(),
                simulacao.receitaBrutaMensal(),
                simulacao.custoFixoSublocacao(),
                simulacao.custoPercentualSublocacao(),
                simulacao.custoTotalSublocacao(),
                simulacao.custosOperacionaisParceiro(),
                simulacao.lucroEstimadoParceiro(),
                simulacao.margemParceiroPercentual(),
                simulacao.status()
        );
    }
}
