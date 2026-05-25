package br.com.atendepro.modules.precificacao.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public record AnaliseMargemLucroPrecificacao(
        PrecoMinimoPrecificacao precoMinimo,
        BigDecimal precoVenda,
        BigDecimal lucroEstimado,
        BigDecimal margemRealPercentual,
        StatusMargemPrecificacao status,
        List<AlertaPrecificacao> alertas
) {

    private static final BigDecimal MARGEM_BAIXA_PERCENTUAL = new BigDecimal("20.00");

    public AnaliseMargemLucroPrecificacao {
        if (precoMinimo == null) {
            throw new IllegalArgumentException("preco minimo da analise de margem e obrigatorio");
        }
        if (precoVenda == null || precoVenda.signum() <= 0) {
            throw new IllegalArgumentException("preco de venda deve ser positivo");
        }
        precoVenda = precoVenda.setScale(2, RoundingMode.HALF_EVEN);
        lucroEstimado = precoVenda.subtract(precoMinimo.precoMinimo()).setScale(2, RoundingMode.HALF_EVEN);
        margemRealPercentual = lucroEstimado
                .divide(precoVenda, 8, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_EVEN);
        status = status(lucroEstimado, margemRealPercentual);
        alertas = List.copyOf(alertas(status));
    }

    public static AnaliseMargemLucroPrecificacao analisar(
            PrecoMinimoPrecificacao precoMinimo,
            BigDecimal precoVenda
    ) {
        return new AnaliseMargemLucroPrecificacao(
                precoMinimo,
                precoVenda,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                StatusMargemPrecificacao.SAUDAVEL,
                List.of()
        );
    }

    private static StatusMargemPrecificacao status(BigDecimal lucroEstimado, BigDecimal margemRealPercentual) {
        if (lucroEstimado.signum() < 0) {
            return StatusMargemPrecificacao.PREJUIZO;
        }
        if (lucroEstimado.signum() == 0) {
            return StatusMargemPrecificacao.EQUILIBRIO;
        }
        if (margemRealPercentual.compareTo(MARGEM_BAIXA_PERCENTUAL) < 0) {
            return StatusMargemPrecificacao.MARGEM_BAIXA;
        }
        return StatusMargemPrecificacao.SAUDAVEL;
    }

    private static List<AlertaPrecificacao> alertas(StatusMargemPrecificacao status) {
        var alertas = new ArrayList<AlertaPrecificacao>();
        if (status == StatusMargemPrecificacao.PREJUIZO) {
            alertas.add(new AlertaPrecificacao(
                    "PRECO_ABAIXO_DO_MINIMO",
                    NivelAlertaPrecificacao.CRITICO,
                    "Preco de venda esta abaixo do custo real."
            ));
        }
        if (status == StatusMargemPrecificacao.EQUILIBRIO) {
            alertas.add(new AlertaPrecificacao(
                    "PRECO_NO_PONTO_DE_EQUILIBRIO",
                    NivelAlertaPrecificacao.ATENCAO,
                    "Preco cobre custos, mas nao gera lucro."
            ));
        }
        if (status == StatusMargemPrecificacao.MARGEM_BAIXA) {
            alertas.add(new AlertaPrecificacao(
                    "MARGEM_BAIXA",
                    NivelAlertaPrecificacao.ATENCAO,
                    "Margem real esta abaixo de 20%."
            ));
        }
        return alertas;
    }
}
