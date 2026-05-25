package br.com.atendepro.modules.precificacao.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.command.SalvarSimulacaoPrecificacaoCommand;
import br.com.atendepro.modules.precificacao.application.result.SimulacaoPrecificacaoResult;

public interface AtualizarSimulacaoPrecificacaoUseCase {

    SimulacaoPrecificacaoResult atualizarSimulacao(UUID simulacaoId, SalvarSimulacaoPrecificacaoCommand command);
}
