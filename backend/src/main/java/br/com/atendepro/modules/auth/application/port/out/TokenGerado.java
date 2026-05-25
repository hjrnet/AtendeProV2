package br.com.atendepro.modules.auth.application.port.out;

import java.time.Instant;

public record TokenGerado(String valor, Instant expiraEm) {
}
