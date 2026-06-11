package br.com.atendepro.modules.mobile.adapter.in.web;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.auth.adapter.out.security.JwtAutenticacaoProperties;
import br.com.atendepro.modules.auth.adapter.out.security.JwtChaveAssinaturaFactory;
import br.com.atendepro.modules.mobile.application.port.in.ConsultarPerfilMobileUseCase;

@RestController
@RequestMapping("/api/mobile")
@Profile("!test")
public class MobileController {

    private final ConsultarPerfilMobileUseCase consultarPerfilMobileUseCase;
    private final JwtDecoder jwtDecoder;

    public MobileController(
            ConsultarPerfilMobileUseCase consultarPerfilMobileUseCase,
            JwtAutenticacaoProperties properties
    ) {
        this.consultarPerfilMobileUseCase = consultarPerfilMobileUseCase;
        this.jwtDecoder = NimbusJwtDecoder
                .withSecretKey(JwtChaveAssinaturaFactory.criarChaveAssinatura(properties.segredo()))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<PerfilMobileResponse> me(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Jwt jwt = jwtDecoder.decode(authorization.substring("Bearer ".length()));
            UUID usuarioId = UUID.fromString(jwt.getSubject());
            return ResponseEntity.ok(PerfilMobileResponse.de(
                    consultarPerfilMobileUseCase.consultarPerfilMobile(usuarioId)
            ));
        } catch (JwtException | IllegalArgumentException ignored) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
