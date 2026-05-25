package br.com.atendepro.modules.suporte.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.suporte.domain.model.ChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;

public record ChamadoSuporteResult(
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

    public static ChamadoSuporteResult de(ChamadoSuporte chamado) {
        return new ChamadoSuporteResult(
                chamado.id(),
                chamado.empresaId(),
                chamado.solicitanteUsuarioId(),
                chamado.solicitanteNome(),
                chamado.solicitanteEmail(),
                chamado.titulo(),
                chamado.descricao(),
                chamado.prioridade(),
                chamado.status(),
                chamado.categoria(),
                chamado.criadoEm(),
                chamado.atualizadoEm()
        );
    }
}
