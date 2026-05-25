package br.com.atendepro.modules.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TokenRecuperacaoSenha(
        UUID id,
        UUID usuarioId,
        String tokenHash,
        Instant expiraEm,
        boolean utilizado,
        Instant criadoEm
) {

    public TokenRecuperacaoSenha {
        if (id == null) {
            throw new IllegalArgumentException("id do token de recuperacao e obrigatorio");
        }
        if (usuarioId == null) {
            throw new IllegalArgumentException("usuario do token de recuperacao e obrigatorio");
        }
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("hash do token de recuperacao e obrigatorio");
        }
        if (expiraEm == null) {
            throw new IllegalArgumentException("expiracao do token de recuperacao e obrigatoria");
        }
        if (criadoEm == null) {
            throw new IllegalArgumentException("criacao do token de recuperacao e obrigatoria");
        }
    }
}
