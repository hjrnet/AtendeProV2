package br.com.atendepro.modules.auth.application.port.out;

import java.time.Instant;

public record RefreshTokenGerado(String valor, String tokenHash, Instant expiraEm) {
}
