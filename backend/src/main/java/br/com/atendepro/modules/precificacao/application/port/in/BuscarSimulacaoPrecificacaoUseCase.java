package br.com.atendepro.modules.precificacao.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.SimulacaoPrecificacaoResult;

public interface BuscarSimulacaoPrecificacaoUseCase {

    Optional<SimulacaoPrecificacaoResult> buscarSimulacaoPorId(UUID simulacaoId);
}
