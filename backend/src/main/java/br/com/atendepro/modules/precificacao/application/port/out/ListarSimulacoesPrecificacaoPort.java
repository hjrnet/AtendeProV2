package br.com.atendepro.modules.precificacao.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarSimulacoesPrecificacaoPort {

    ResultadoPaginado<SimulacaoPrecificacao> listarSimulacoes(UUID empresaId, Paginacao paginacao, String busca);
}
