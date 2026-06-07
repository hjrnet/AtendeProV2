package br.com.atendepro.modules.suporte.application.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;

public record RespostaAssistidaChamadoSuporteResult(
        UUID chamadoId,
        UUID empresaId,
        String titulo,
        String categoriaSugerida,
        PrioridadeChamadoSuporte prioridadeSugerida,
        StatusChamadoSuporte statusSugerido,
        String resumo,
        String respostaSugerida,
        List<String> proximasAcoes,
        Instant geradoEm
) {

    public RespostaAssistidaChamadoSuporteResult {
        proximasAcoes = List.copyOf(proximasAcoes);
    }
}
