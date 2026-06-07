package br.com.atendepro.modules.suporte.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.suporte.application.result.RespostaAssistidaChamadoSuporteResult;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;

public record RespostaAssistidaChamadoSuporteResponse(
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

    public static RespostaAssistidaChamadoSuporteResponse de(RespostaAssistidaChamadoSuporteResult result) {
        return new RespostaAssistidaChamadoSuporteResponse(
                result.chamadoId(),
                result.empresaId(),
                result.titulo(),
                result.categoriaSugerida(),
                result.prioridadeSugerida(),
                result.statusSugerido(),
                result.resumo(),
                result.respostaSugerida(),
                result.proximasAcoes(),
                result.geradoEm()
        );
    }
}
