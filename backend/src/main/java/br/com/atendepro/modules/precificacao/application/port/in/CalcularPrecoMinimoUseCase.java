package br.com.atendepro.modules.precificacao.application.port.in;

import br.com.atendepro.modules.precificacao.application.command.CalcularPrecoMinimoCommand;
import br.com.atendepro.modules.precificacao.application.result.PrecoMinimoPrecificacaoResult;

public interface CalcularPrecoMinimoUseCase {

    PrecoMinimoPrecificacaoResult calcularPrecoMinimo(CalcularPrecoMinimoCommand command);
}
