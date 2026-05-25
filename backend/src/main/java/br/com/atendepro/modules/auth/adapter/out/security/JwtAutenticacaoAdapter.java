package br.com.atendepro.modules.auth.adapter.out.security;

import java.time.Clock;
import java.time.Instant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import br.com.atendepro.modules.auth.application.port.out.GerarTokenAutenticacaoPort;
import br.com.atendepro.modules.auth.application.port.out.TokenGerado;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

@Component
public class JwtAutenticacaoAdapter implements GerarTokenAutenticacaoPort {

    private final JwtAutenticacaoProperties properties;
    private final Clock clock;

    public JwtAutenticacaoAdapter(JwtAutenticacaoProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    public TokenGerado gerarAccessToken(UsuarioAutenticacao usuario) {
        Instant emitidoEm = Instant.now(clock);
        Instant expiraEm = emitidoEm.plusSeconds(properties.expiracaoMinutos() * 60L);
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer(properties.emissor())
                .issuedAt(emitidoEm)
                .expiresAt(expiraEm)
                .subject(usuario.id().toString())
                .claim("email", usuario.email().valor())
                .claim("nome", usuario.nome())
                .claim("perfis", usuario.perfis().stream().map(Enum::name).toList())
                .claim("authorities", usuario.authorities().stream().sorted().toList());
        if (usuario.empresaId() != null) {
            claimsBuilder.claim("empresaId", usuario.empresaId().toString());
        }
        JwtClaimsSet claims = claimsBuilder.build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder().encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenGerado(token, expiraEm);
    }

    private JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(JwtChaveAssinaturaFactory.criarChaveAssinatura(properties.segredo())));
    }
}
