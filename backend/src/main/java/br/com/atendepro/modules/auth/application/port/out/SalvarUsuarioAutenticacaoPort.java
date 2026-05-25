package br.com.atendepro.modules.auth.application.port.out;

import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

public interface SalvarUsuarioAutenticacaoPort {

    void salvarUsuarioAutenticacao(UsuarioAutenticacao usuario);
}
