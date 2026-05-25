package br.com.atendepro.modules.precificacao.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CustoRealPrecificacao(
        CalculoPrecificacao calculoBase,
        int duracaoMinutos,
        BigDecimal custoInsumos,
        BigDecimal custoSala,
        BigDecimal custoTempoProfissional,
        BigDecimal custoDeslocamento,
        BigDecimal custoAlimentacao,
        BigDecimal taxas
) {

    public CustoRealPrecificacao {
        if (calculoBase == null) {
            throw new IllegalArgumentException("calculo base da precificacao e obrigatorio");
        }
        if (duracaoMinutos < 1) {
            throw new IllegalArgumentException("duracao do procedimento deve ser positiva");
        }
        custoInsumos = valorMonetario(custoInsumos, "custo de insumos");
        custoSala = valorMonetario(custoSala, "custo de sala");
        custoTempoProfissional = valorMonetario(custoTempoProfissional, "custo de tempo profissional");
        custoDeslocamento = valorMonetario(custoDeslocamento, "custo de deslocamento");
        custoAlimentacao = valorMonetario(custoAlimentacao, "custo de alimentacao");
        taxas = valorMonetario(taxas, "taxas");
    }

    public static CustoRealPrecificacao calcular(
            UUID empresaId,
            UUID servicoProcedimentoId,
            String nomeProcedimento,
            int duracaoMinutos,
            BigDecimal custoInsumos,
            BigDecimal custoSalaPorHora,
            BigDecimal valorHoraProfissional,
            BigDecimal custoDeslocamento,
            BigDecimal custoAlimentacao,
            BigDecimal taxas,
            Instant calculadoEm
    ) {
        BigDecimal custoSala = ratearPorDuracao(custoSalaPorHora, duracaoMinutos, "custo de sala por hora");
        BigDecimal custoTempoProfissional = ratearPorDuracao(
                valorHoraProfissional,
                duracaoMinutos,
                "valor hora profissional"
        );
        BigDecimal insumos = valorMonetario(custoInsumos, "custo de insumos");
        BigDecimal deslocamento = valorMonetario(custoDeslocamento, "custo de deslocamento");
        BigDecimal alimentacao = valorMonetario(custoAlimentacao, "custo de alimentacao");
        BigDecimal taxasNormalizadas = valorMonetario(taxas, "taxas");
        CalculoPrecificacao calculoBase = CalculoPrecificacao.calcular(
                empresaId,
                servicoProcedimentoId,
                nomeProcedimento,
                List.of(
                        new ItemCustoPrecificacao("Insumos", CategoriaItemPrecificacao.INSUMO, insumos),
                        new ItemCustoPrecificacao("Sala", CategoriaItemPrecificacao.SALA, custoSala),
                        new ItemCustoPrecificacao(
                                "Tempo profissional",
                                CategoriaItemPrecificacao.TEMPO_PROFISSIONAL,
                                custoTempoProfissional
                        ),
                        new ItemCustoPrecificacao(
                                "Deslocamento",
                                CategoriaItemPrecificacao.DESLOCAMENTO,
                                deslocamento
                        ),
                        new ItemCustoPrecificacao("Alimentacao", CategoriaItemPrecificacao.ALIMENTACAO, alimentacao),
                        new ItemCustoPrecificacao("Taxas", CategoriaItemPrecificacao.TAXA, taxasNormalizadas)
                ),
                calculadoEm
        );
        return new CustoRealPrecificacao(
                calculoBase,
                duracaoMinutos,
                insumos,
                custoSala,
                custoTempoProfissional,
                deslocamento,
                alimentacao,
                taxasNormalizadas
        );
    }

    public BigDecimal custoTotal() {
        return calculoBase.custoTotal();
    }

    private static BigDecimal ratearPorDuracao(BigDecimal valorPorHora, int duracaoMinutos, String campo) {
        BigDecimal valorNormalizado = valorMonetario(valorPorHora, campo);
        return valorNormalizado
                .multiply(BigDecimal.valueOf(duracaoMinutos))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal valorMonetario(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " nao pode ser negativo");
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }
}
