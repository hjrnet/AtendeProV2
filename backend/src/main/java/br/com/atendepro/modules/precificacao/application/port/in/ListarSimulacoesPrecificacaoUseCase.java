package br.com.atendepro.modules.precificacao.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.SimulacaoPrecificacaoResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarSimulacoesPrecificacaoUseCase {

    ResultadoPaginado<SimulacaoPrecificacaoResult> listarSimulacoes(
            UUID empresaId,
            Paginacao paginacao,
            String busca
    );
}
