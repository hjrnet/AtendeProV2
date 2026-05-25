package br.com.atendepro.modules.precificacao.application.port.in;

import br.com.atendepro.modules.precificacao.application.command.CalcularPrecoRecomendadoCommand;
import br.com.atendepro.modules.precificacao.application.result.PrecoRecomendadoPrecificacaoResult;

public interface CalcularPrecoRecomendadoUseCase {

    PrecoRecomendadoPrecificacaoResult calcularPrecoRecomendado(CalcularPrecoRecomendadoCommand command);
}
