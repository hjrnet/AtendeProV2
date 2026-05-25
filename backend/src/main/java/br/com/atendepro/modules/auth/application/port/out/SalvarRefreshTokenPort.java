package br.com.atendepro.modules.auth.application.port.out;

import br.com.atendepro.modules.auth.domain.model.RefreshTokenAutenticacao;

public interface SalvarRefreshTokenPort {

    void salvarRefreshToken(RefreshTokenAutenticacao refreshToken);
}
