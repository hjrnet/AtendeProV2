package br.com.atendepro.modules.precificacao.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;

public interface CarregarSimulacaoPrecificacaoPorIdPort {

    Optional<SimulacaoPrecificacao> carregarSimulacaoPorId(UUID simulacaoId);
}
