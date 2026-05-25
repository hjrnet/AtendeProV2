package br.com.atendepro.modules.auth.application.port.out;

import java.time.Instant;

public record TokenRecuperacaoSenhaGerado(String valor, String tokenHash, Instant expiraEm) {
}
