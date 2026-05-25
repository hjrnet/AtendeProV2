package br.com.atendepro.modules.auth.application.port.out;

import br.com.atendepro.modules.auth.domain.model.TokenRecuperacaoSenha;

public interface SalvarTokenRecuperacaoSenhaPort {

    void salvarTokenRecuperacaoSenha(TokenRecuperacaoSenha token);
}
