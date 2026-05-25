package br.com.atendepro.modules.auth.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface RevogarRefreshTokenPort {

    void revogarRefreshToken(UUID refreshTokenId, Instant revogadoEm);
}
