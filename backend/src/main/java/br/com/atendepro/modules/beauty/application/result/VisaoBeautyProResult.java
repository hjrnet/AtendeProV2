package br.com.atendepro.modules.beauty.application.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.StatusOperacionalBeautyPro;

public record VisaoBeautyProResult(
        UUID empresaId,
        String empresaNome,
        StatusOperacionalBeautyPro statusOperacional,
        List<IndicadorBeautyProResult> indicadores,
        List<AtalhoBeautyProResult> atalhosPrioritarios,
        List<AtalhoBeautyProResult> proximasEvolucoes,
        List<ClienteBeautyResumoResult> clientesRecentes,
        Instant atualizadoEm
) {
}
