package br.com.atendepro.modules.beauty.application.result;

import java.time.Instant;

public record ResumoProntuarioBeautyProResult(
        long fichasEsteticas,
        long consultasFuturas,
        long documentos,
        String statusFichaEstetica,
        String statusContraindicacoes,
        Instant ultimaConsultaEm
) {
}
