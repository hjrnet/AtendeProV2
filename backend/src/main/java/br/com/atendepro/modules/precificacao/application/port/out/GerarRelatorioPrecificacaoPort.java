package br.com.atendepro.modules.precificacao.application.port.out;

import br.com.atendepro.modules.precificacao.application.result.RelatorioPrecificacaoResult;
import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;

public interface GerarRelatorioPrecificacaoPort {

    RelatorioPrecificacaoResult gerarRelatorio(SimulacaoPrecificacao simulacao);
}
