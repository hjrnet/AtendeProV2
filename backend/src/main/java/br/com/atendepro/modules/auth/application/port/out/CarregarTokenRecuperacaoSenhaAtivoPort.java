package br.com.atendepro.modules.auth.application.port.out;

import java.time.Instant;
import java.util.Optional;

import br.com.atendepro.modules.auth.domain.model.TokenRecuperacaoSenha;

public interface CarregarTokenRecuperacaoSenhaAtivoPort {

    Optional<TokenRecuperacaoSenha> carregarTokenRecuperacaoSenhaAtivo(String tokenHash, Instant agora);
}
