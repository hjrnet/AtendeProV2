package br.com.atendepro.modules.precificacao.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.RelatorioPrecificacaoResult;

public interface GerarRelatorioPrecificacaoUseCase {

    RelatorioPrecificacaoResult gerarRelatorio(UUID simulacaoId);
}
