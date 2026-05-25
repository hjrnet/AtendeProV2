package br.com.atendepro.modules.spaces.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.CustoHoraSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;

public record CustoHoraSpacesResponse(
        UUID empresaId,
        UUID recursoId,
        String nomeRecurso,
        TipoRecursoSpaces tipoRecurso,
        BigDecimal custoFixoMensal,
        int diasDisponiveisMes,
        BigDecimal horasDisponiveisDia,
        BigDecimal horasDisponiveisMes,
        BigDecimal custoHora
) {

    static CustoHoraSpacesResponse de(CustoHoraSpacesResult result) {
        return new CustoHoraSpacesResponse(
                result.empresaId(),
                result.recursoId(),
                result.nomeRecurso(),
                result.tipoRecurso(),
                result.custoFixoMensal(),
                result.diasDisponiveisMes(),
                result.horasDisponiveisDia(),
                result.horasDisponiveisMes(),
                result.custoHora()
        );
    }
}
