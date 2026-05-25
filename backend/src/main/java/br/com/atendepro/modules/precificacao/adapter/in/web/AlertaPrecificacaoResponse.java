package br.com.atendepro.modules.precificacao.adapter.in.web;

import br.com.atendepro.modules.precificacao.application.result.AlertaPrecificacaoResult;
import br.com.atendepro.modules.precificacao.domain.model.NivelAlertaPrecificacao;

public record AlertaPrecificacaoResponse(
        String codigo,
        NivelAlertaPrecificacao nivel,
        String mensagem
) {

    public static AlertaPrecificacaoResponse de(AlertaPrecificacaoResult result) {
        return new AlertaPrecificacaoResponse(result.codigo(), result.nivel(), result.mensagem());
    }
}
