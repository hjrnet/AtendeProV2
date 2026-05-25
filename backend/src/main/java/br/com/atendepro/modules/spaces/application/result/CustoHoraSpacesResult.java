package br.com.atendepro.modules.spaces.application.result;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.CustoHoraSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;

public record CustoHoraSpacesResult(
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

    public static CustoHoraSpacesResult de(UUID empresaId, CustoHoraSpaces custoHora) {
        return new CustoHoraSpacesResult(
                empresaId,
                custoHora.recursoId(),
                custoHora.nomeRecurso(),
                custoHora.tipoRecurso(),
                custoHora.custoFixoMensal(),
                custoHora.diasDisponiveisMes(),
                custoHora.horasDisponiveisDia(),
                custoHora.horasDisponiveisMes(),
                custoHora.custoHora()
        );
    }
}
