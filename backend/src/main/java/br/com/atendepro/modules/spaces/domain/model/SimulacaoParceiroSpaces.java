package br.com.atendepro.modules.spaces.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public record SimulacaoParceiroSpaces(
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

    private static final BigDecimal CEM = new BigDecimal("100.00");
    private static final BigDecimal MARGEM_BAIXA = new BigDecimal("20.00");

    public static SimulacaoParceiroSpaces calcular(
            PacoteSublocacaoSpaces pacote,
            int quantidadePacotesMes,
            int atendimentosMes,
            BigDecimal ticketMedio,
            BigDecimal custosOperacionaisParceiro
    ) {
        if (pacote == null) {
            throw new IllegalArgumentException("pacote de sublocacao e obrigatorio para simular parceiro");
        }
        if (quantidadePacotesMes <= 0) {
            throw new IllegalArgumentException("quantidade de pacotes no mes deve ser positiva");
        }
        if (atendimentosMes <= 0) {
            throw new IllegalArgumentException("atendimentos no mes devem ser positivos");
        }
        validarNaoNegativo(ticketMedio, "ticket medio");
        validarNaoNegativo(custosOperacionaisParceiro, "custos operacionais do parceiro");
        BigDecimal receitaBruta = ticketMedio.multiply(BigDecimal.valueOf(atendimentosMes)).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal custoFixo = pacote.valorFixo().multiply(BigDecimal.valueOf(quantidadePacotesMes)).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal custoPercentual = receitaBruta
                .multiply(pacote.percentualReceita())
                .divide(CEM, 2, RoundingMode.HALF_EVEN);
        BigDecimal custoTotalSublocacao = custoFixo.add(custoPercentual).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal custosOperacionais = custosOperacionaisParceiro.setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal lucro = receitaBruta.subtract(custoTotalSublocacao).subtract(custosOperacionais).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal margem = receitaBruta.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN)
                : lucro.multiply(CEM).divide(receitaBruta, 2, RoundingMode.HALF_EVEN);
        return new SimulacaoParceiroSpaces(
                pacote.id(),
                pacote.nome(),
                pacote.tipo(),
                quantidadePacotesMes,
                atendimentosMes,
                ticketMedio.setScale(2, RoundingMode.HALF_EVEN),
                receitaBruta,
                custoFixo,
                custoPercentual,
                custoTotalSublocacao,
                custosOperacionais,
                lucro,
                margem,
                definirStatus(lucro, margem)
        );
    }

    private static StatusSimulacaoParceiroSpaces definirStatus(BigDecimal lucro, BigDecimal margem) {
        if (lucro.compareTo(BigDecimal.ZERO) < 0) {
            return StatusSimulacaoParceiroSpaces.PREJUIZO;
        }
        if (margem.compareTo(MARGEM_BAIXA) < 0) {
            return StatusSimulacaoParceiroSpaces.MARGEM_BAIXA;
        }
        return StatusSimulacaoParceiroSpaces.SAUDAVEL;
    }

    private static void validarNaoNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(campo + " nao pode ser negativo");
        }
    }
}
