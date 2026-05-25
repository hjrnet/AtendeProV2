package br.com.atendepro.modules.precificacao.application.port.out;

import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;

public interface AtualizarSimulacaoPrecificacaoPort {

    void atualizarSimulacao(SimulacaoPrecificacao simulacao);
}
