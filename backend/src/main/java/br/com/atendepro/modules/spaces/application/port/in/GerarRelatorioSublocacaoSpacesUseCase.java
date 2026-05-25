package br.com.atendepro.modules.spaces.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.RelatorioSublocacaoSpacesResult;

public interface GerarRelatorioSublocacaoSpacesUseCase {

    RelatorioSublocacaoSpacesResult gerarRelatorio(UUID empresaId);
}
