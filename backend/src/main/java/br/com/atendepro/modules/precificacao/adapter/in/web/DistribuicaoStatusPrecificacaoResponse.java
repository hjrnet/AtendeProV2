package br.com.atendepro.modules.precificacao.adapter.in.web;

import br.com.atendepro.modules.precificacao.application.result.DistribuicaoStatusPrecificacaoResult;

public record DistribuicaoStatusPrecificacaoResponse(
        String status,
        long total
) {

    public static DistribuicaoStatusPrecificacaoResponse de(DistribuicaoStatusPrecificacaoResult result) {
        return new DistribuicaoStatusPrecificacaoResponse(result.status(), result.total());
    }
}
