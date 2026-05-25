package br.com.atendepro.modules.spaces.application.port.out;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.IndicadoresSublocacaoSpacesResult;

public interface CarregarIndicadoresSublocacaoSpacesPort {

    IndicadoresSublocacaoSpacesResult carregarIndicadores(UUID empresaId, Instant periodoInicio, Instant periodoFim);
}
