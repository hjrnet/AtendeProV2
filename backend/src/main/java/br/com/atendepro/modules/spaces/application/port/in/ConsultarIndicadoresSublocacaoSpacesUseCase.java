package br.com.atendepro.modules.spaces.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.IndicadoresSublocacaoSpacesResult;

public interface ConsultarIndicadoresSublocacaoSpacesUseCase {

    IndicadoresSublocacaoSpacesResult consultarIndicadores(UUID empresaId);
}
