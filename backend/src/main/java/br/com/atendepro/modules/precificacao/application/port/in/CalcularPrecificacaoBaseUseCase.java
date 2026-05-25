package br.com.atendepro.modules.precificacao.application.port.in;

import br.com.atendepro.modules.precificacao.application.command.CalcularPrecificacaoBaseCommand;
import br.com.atendepro.modules.precificacao.application.result.CalculoPrecificacaoBaseResult;

public interface CalcularPrecificacaoBaseUseCase {

    CalculoPrecificacaoBaseResult calcularPrecificacaoBase(CalcularPrecificacaoBaseCommand command);
}
