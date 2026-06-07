package br.com.atendepro.modules.precificacao.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.SugestoesPrecificacaoAssistidaResult;

public interface GerarSugestoesPrecificacaoAssistidaUseCase {

    SugestoesPrecificacaoAssistidaResult gerarSugestoes(UUID simulacaoId);
}
