package br.com.atendepro.modules.spaces.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public record CustoHoraSpaces(
        UUID recursoId,
        String nomeRecurso,
        TipoRecursoSpaces tipoRecurso,
        BigDecimal custoFixoMensal,
        int diasDisponiveisMes,
        BigDecimal horasDisponiveisDia,
        BigDecimal horasDisponiveisMes,
        BigDecimal custoHora
) {

    public CustoHoraSpaces {
        validarPositivo(custoFixoMensal, "custo fixo mensal");
        if (diasDisponiveisMes <= 0) {
            throw new IllegalArgumentException("dias disponiveis do espaco devem ser positivos");
        }
        validarPositivo(horasDisponiveisDia, "horas disponiveis por dia");
        validarPositivo(horasDisponiveisMes, "horas disponiveis no mes");
        validarPositivo(custoHora, "custo por hora");
    }

    public static CustoHoraSpaces calcular(
            UUID recursoId,
            String nomeRecurso,
            TipoRecursoSpaces tipoRecurso,
            BigDecimal custoFixoMensal,
            int diasDisponiveisMes,
            BigDecimal horasDisponiveisDia
    ) {
        validarPositivo(custoFixoMensal, "custo fixo mensal");
        if (diasDisponiveisMes <= 0) {
            throw new IllegalArgumentException("dias disponiveis do espaco devem ser positivos");
        }
        validarPositivo(horasDisponiveisDia, "horas disponiveis por dia");
        BigDecimal horasMes = horasDisponiveisDia
                .multiply(BigDecimal.valueOf(diasDisponiveisMes))
                .setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal custoHora = custoFixoMensal.divide(horasMes, 2, RoundingMode.CEILING);
        return new CustoHoraSpaces(
                recursoId,
                nomeRecurso,
                tipoRecurso,
                custoFixoMensal.setScale(2, RoundingMode.HALF_EVEN),
                diasDisponiveisMes,
                horasDisponiveisDia.setScale(2, RoundingMode.HALF_EVEN),
                horasMes,
                custoHora
        );
    }

    private static void validarPositivo(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(campo + " deve ser positivo");
        }
    }
}
