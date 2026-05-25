package br.com.atendepro.modules.auth.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.atendepro.modules.auth.application.result.SolicitarRecuperacaoSenhaResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SolicitarRecuperacaoSenhaResponse(
        String mensagem,
        String tokenTesteLocal
) {

    static SolicitarRecuperacaoSenhaResponse de(SolicitarRecuperacaoSenhaResult result) {
        return new SolicitarRecuperacaoSenhaResponse(
                "Se o email existir, enviaremos instrucoes para redefinir a senha.",
                result.tokenTesteLocal()
        );
    }
}
