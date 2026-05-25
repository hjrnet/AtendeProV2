package br.com.atendepro.modules.spaces.application.port.out;

import br.com.atendepro.modules.spaces.application.result.IndicadoresSublocacaoSpacesResult;
import br.com.atendepro.modules.spaces.application.result.RelatorioSublocacaoSpacesResult;

public interface GerarPdfRelatorioSublocacaoSpacesPort {

    RelatorioSublocacaoSpacesResult gerarPdf(IndicadoresSublocacaoSpacesResult indicadores);
}
