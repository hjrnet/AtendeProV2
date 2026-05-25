package br.com.atendepro.modules.precificacao.application.result;

import br.com.atendepro.modules.precificacao.domain.model.AlertaPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.NivelAlertaPrecificacao;

public record AlertaPrecificacaoResult(
        String codigo,
        NivelAlertaPrecificacao nivel,
        String mensagem
) {

    public static AlertaPrecificacaoResult de(AlertaPrecificacao alerta) {
        return new AlertaPrecificacaoResult(alerta.codigo(), alerta.nivel(), alerta.mensagem());
    }
}
