package br.com.atendepro.modules.precificacao.adapter.in.web;

import br.com.atendepro.modules.precificacao.application.result.SugestaoPrecificacaoAssistidaResult;

public record SugestaoPrecificacaoAssistidaResponse(
        String tipo,
        String titulo,
        String descricao
) {

    public static SugestaoPrecificacaoAssistidaResponse de(SugestaoPrecificacaoAssistidaResult result) {
        return new SugestaoPrecificacaoAssistidaResponse(result.tipo(), result.titulo(), result.descricao());
    }
}
