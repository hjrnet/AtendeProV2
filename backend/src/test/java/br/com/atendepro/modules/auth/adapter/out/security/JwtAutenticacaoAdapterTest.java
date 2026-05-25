package br.com.atendepro.modules.auth.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

class JwtAutenticacaoAdapterTest {

    @Test
    void deveGerarAccessTokenJwt() {
        JwtAutenticacaoAdapter adapter = new JwtAutenticacaoAdapter(
                new JwtAutenticacaoProperties("atendepro-test", "segredo-de-teste", 30, 7, 30, false),
                Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC)
        );

        var token = adapter.gerarAccessToken(usuario());

        assertThat(token.valor()).contains(".");
        assertThat(token.expiraEm()).isEqualTo(Instant.parse("2026-05-25T00:30:00Z"));
        var decoder = NimbusJwtDecoder
                .withSecretKey(JwtChaveAssinaturaFactory.criarChaveAssinatura("segredo-de-teste"))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(jwt -> OAuth2TokenValidatorResult.success());
        var jwt = decoder.decode(token.valor());
        assertThat(jwt.getClaimAsStringList("authorities"))
                .contains("empresa:cadastrar", "empresa:administrador:cadastrar");
    }

    private UsuarioAutenticacao usuario() {
        return new UsuarioAutenticacao(
                UUID.fromString("3d0fbc6f-1770-4d05-a0e6-e21efbcaf606"),
                EmailUsuario.de("admin@atendepro.local"),
                "Admin",
                "hash",
                Set.of(PerfilAcesso.SUPER_ADMIN),
                true,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }
}
