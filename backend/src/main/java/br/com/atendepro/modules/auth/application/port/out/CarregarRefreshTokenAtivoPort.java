package br.com.atendepro.modules.auth.application.port.out;

import java.time.Instant;
import java.util.Optional;

import br.com.atendepro.modules.auth.domain.model.RefreshTokenAutenticacao;

public interface CarregarRefreshTokenAtivoPort {

    Optional<RefreshTokenAutenticacao> carregarRefreshTokenAtivo(String tokenHash, Instant agora);
}
