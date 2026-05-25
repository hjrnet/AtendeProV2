package br.com.atendepro.shared.adapter.in.web.error;

import java.time.Instant;
import java.util.List;

public record ErroApiResponse(
        String codigo,
        String mensagem,
        String path,
        Instant timestamp,
        List<CampoErroResponse> campos
) {

    public ErroApiResponse {
        campos = List.copyOf(campos);
    }
}
