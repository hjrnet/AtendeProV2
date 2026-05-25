package br.com.atendepro.modules.precificacao.application.port.in;

import br.com.atendepro.modules.precificacao.application.command.SalvarSimulacaoPrecificacaoCommand;
import br.com.atendepro.modules.precificacao.application.result.SimulacaoPrecificacaoResult;

public interface SalvarSimulacaoPrecificacaoUseCase {

    SimulacaoPrecificacaoResult salvarSimulacao(SalvarSimulacaoPrecificacaoCommand command);
}
