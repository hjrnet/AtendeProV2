package br.com.atendepro.modules.precificacao.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public record SimulacaoPrecificacao(
        UUID id,
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
        BigDecimal margemDesejadaPercentual,
        BigDecimal precoVenda,
        BigDecimal custoTotal,
        BigDecimal precoMinimo,
        BigDecimal precoRecomendado,
        BigDecimal lucroEstimado,
        BigDecimal margemRealPercentual,
        StatusMargemPrecificacao statusMargem,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public SimulacaoPrecificacao {
        if (id == null) {
            throw new IllegalArgumentException("id da simulacao e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa da simulacao e obrigatoria");
        }
        if (nomeProcedimento == null || nomeProcedimento.isBlank()) {
            throw new IllegalArgumentException("nome do procedimento e obrigatorio");
        }
        if (duracaoMinutos < 1) {
            throw new IllegalArgumentException("duracao da simulacao deve ser positiva");
        }
        if (statusMargem == null) {
            throw new IllegalArgumentException("status da margem e obrigatorio");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas da simulacao sao obrigatorias");
        }
        nomeProcedimento = nomeProcedimento.trim();
        custoInsumos = valorMonetario(custoInsumos, "custo de insumos");
        custoSalaPorHora = valorMonetario(custoSalaPorHora, "custo de sala por hora");
        valorHoraProfissional = valorMonetario(valorHoraProfissional, "valor hora profissional");
        custoDeslocamento = valorMonetario(custoDeslocamento, "custo de deslocamento");
        custoAlimentacao = valorMonetario(custoAlimentacao, "custo de alimentacao");
        taxas = valorMonetario(taxas, "taxas");
        margemDesejadaPercentual = percentual(margemDesejadaPercentual, "margem desejada");
        precoVenda = valorMonetario(precoVenda, "preco de venda");
        custoTotal = valorMonetario(custoTotal, "custo total");
        precoMinimo = valorMonetario(precoMinimo, "preco minimo");
        precoRecomendado = valorMonetario(precoRecomendado, "preco recomendado");
        lucroEstimado = valorMonetarioComSinal(lucroEstimado, "lucro estimado");
        margemRealPercentual = percentualComSinal(margemRealPercentual, "margem real");
    }

    public static SimulacaoPrecificacao registrar(
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
            BigDecimal margemDesejadaPercentual,
            BigDecimal precoVenda,
            PrecoRecomendadoPrecificacao precoRecomendado,
            AnaliseMargemLucroPrecificacao analise,
            Instant agora
    ) {
        return criar(
                UUID.randomUUID(),
                empresaId,
                servicoProcedimentoId,
                nomeProcedimento,
                duracaoMinutos,
                custoInsumos,
                custoSalaPorHora,
                valorHoraProfissional,
                custoDeslocamento,
                custoAlimentacao,
                taxas,
                margemDesejadaPercentual,
                precoVenda,
                precoRecomendado,
                analise,
                true,
                agora,
                agora
        );
    }

    public SimulacaoPrecificacao editar(
            String nomeProcedimento,
            int duracaoMinutos,
            BigDecimal custoInsumos,
            BigDecimal custoSalaPorHora,
            BigDecimal valorHoraProfissional,
            BigDecimal custoDeslocamento,
            BigDecimal custoAlimentacao,
            BigDecimal taxas,
            BigDecimal margemDesejadaPercentual,
            BigDecimal precoVenda,
            PrecoRecomendadoPrecificacao precoRecomendado,
            AnaliseMargemLucroPrecificacao analise,
            Instant agora
    ) {
        return criar(
                id,
                empresaId,
                servicoProcedimentoId,
                nomeProcedimento,
                duracaoMinutos,
                custoInsumos,
                custoSalaPorHora,
                valorHoraProfissional,
                custoDeslocamento,
                custoAlimentacao,
                taxas,
                margemDesejadaPercentual,
                precoVenda,
                precoRecomendado,
                analise,
                ativo,
                criadoEm,
                agora
        );
    }

    private static SimulacaoPrecificacao criar(
            UUID id,
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
            BigDecimal margemDesejadaPercentual,
            BigDecimal precoVenda,
            PrecoRecomendadoPrecificacao precoRecomendado,
            AnaliseMargemLucroPrecificacao analise,
            boolean ativo,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
        return new SimulacaoPrecificacao(
                id,
                empresaId,
                servicoProcedimentoId,
                nomeProcedimento,
                duracaoMinutos,
                custoInsumos,
                custoSalaPorHora,
                valorHoraProfissional,
                custoDeslocamento,
                custoAlimentacao,
                taxas,
                margemDesejadaPercentual,
                precoVenda,
                analise.precoMinimo().custoReal().custoTotal(),
                analise.precoMinimo().precoMinimo(),
                precoRecomendado.precoRecomendado(),
                analise.lucroEstimado(),
                analise.margemRealPercentual(),
                analise.status(),
                ativo,
                criadoEm,
                atualizadoEm
        );
    }

    private static BigDecimal valorMonetario(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " nao pode ser negativo");
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal valorMonetarioComSinal(BigDecimal valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal percentual(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " nao pode ser negativo");
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal percentualComSinal(BigDecimal valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " e obrigatoria");
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }
}
