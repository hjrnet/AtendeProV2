package br.com.atendepro.modules.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenAutenticacao(
        UUID id,
        UUID usuarioId,
        String tokenHash,
        Instant expiraEm,
        boolean revogado,
        Instant criadoEm
) {

    public RefreshTokenAutenticacao {
        if (id == null) {
            throw new IllegalArgumentException("id do refresh token e obrigatorio");
        }
        if (usuarioId == null) {
            throw new IllegalArgumentException("usuario do refresh token e obrigatorio");
        }
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("hash do refresh token e obrigatorio");
        }
        if (expiraEm == null) {
            throw new IllegalArgumentException("expiracao do refresh token e obrigatoria");
        }
        if (criadoEm == null) {
            throw new IllegalArgumentException("criacao do refresh token e obrigatoria");
        }
    }

    public boolean expiradoEm(Instant agora) {
        return !expiraEm.isAfter(agora);
    }

    public boolean ativoEm(Instant agora) {
        return !revogado && !expiradoEm(agora);
    }
}
