package br.com.atendepro.modules.auth.application.port.out;

public interface HashRefreshTokenPort {

    String gerarHashRefreshToken(String refreshToken);
}
