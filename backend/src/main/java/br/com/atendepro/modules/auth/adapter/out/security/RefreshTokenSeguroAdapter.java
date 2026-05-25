package br.com.atendepro.modules.auth.adapter.out.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import br.com.atendepro.modules.auth.application.port.out.GerarRefreshTokenPort;
import br.com.atendepro.modules.auth.application.port.out.HashRefreshTokenPort;
import br.com.atendepro.modules.auth.application.port.out.RefreshTokenGerado;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

@Component
public class RefreshTokenSeguroAdapter implements GerarRefreshTokenPort, HashRefreshTokenPort {

    private static final int TAMANHO_BYTES = 48;

    private final JwtAutenticacaoProperties properties;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenSeguroAdapter(JwtAutenticacaoProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    public RefreshTokenGerado gerarRefreshToken(UsuarioAutenticacao usuario) {
        byte[] bytes = new byte[TAMANHO_BYTES];
        secureRandom.nextBytes(bytes);
        String valor = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return new RefreshTokenGerado(
                valor,
                gerarHashRefreshToken(valor),
                Instant.now(clock).plusSeconds(properties.refreshExpiracaoDias() * 24L * 60L * 60L)
        );
    }

    @Override
    public String gerarHashRefreshToken(String refreshToken) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("algoritmo de hash indisponivel", exception);
        }
    }
}
