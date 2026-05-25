package br.com.atendepro.modules.suporte.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.suporte.application.result.ChamadoSuporteResult;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;

public record ChamadoSuporteResponse(
        UUID id,
        UUID empresaId,
        UUID solicitanteUsuarioId,
        String solicitanteNome,
        String solicitanteEmail,
        String titulo,
        String descricao,
        PrioridadeChamadoSuporte prioridade,
        StatusChamadoSuporte status,
        String categoria,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static ChamadoSuporteResponse de(ChamadoSuporteResult result) {
        return new ChamadoSuporteResponse(
                result.id(),
                result.empresaId(),
                result.solicitanteUsuarioId(),
                result.solicitanteNome(),
                result.solicitanteEmail(),
                result.titulo(),
                result.descricao(),
                result.prioridade(),
                result.status(),
                result.categoria(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
