package br.com.atendepro.modules.auth.adapter.in.web;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.auth.adapter.out.security.JwtAutenticacaoProperties;
import br.com.atendepro.modules.auth.adapter.out.security.JwtChaveAssinaturaFactory;
import br.com.atendepro.modules.auth.application.port.in.AutenticarUsuarioUseCase;
import br.com.atendepro.modules.auth.application.port.in.RedefinirSenhaUseCase;
import br.com.atendepro.modules.auth.application.port.in.RenovarSessaoUseCase;
import br.com.atendepro.modules.auth.application.port.in.SolicitarRecuperacaoSenhaUseCase;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Profile("!test")
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final RenovarSessaoUseCase renovarSessaoUseCase;
    private final SolicitarRecuperacaoSenhaUseCase solicitarRecuperacaoSenhaUseCase;
    private final RedefinirSenhaUseCase redefinirSenhaUseCase;
    private final JwtDecoder jwtDecoder;

    public AuthController(
            AutenticarUsuarioUseCase autenticarUsuarioUseCase,
            RenovarSessaoUseCase renovarSessaoUseCase,
            SolicitarRecuperacaoSenhaUseCase solicitarRecuperacaoSenhaUseCase,
            RedefinirSenhaUseCase redefinirSenhaUseCase,
            JwtAutenticacaoProperties properties
    ) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.renovarSessaoUseCase = renovarSessaoUseCase;
        this.solicitarRecuperacaoSenhaUseCase = solicitarRecuperacaoSenhaUseCase;
        this.redefinirSenhaUseCase = redefinirSenhaUseCase;
        this.jwtDecoder = NimbusJwtDecoder
                .withSecretKey(JwtChaveAssinaturaFactory.criarChaveAssinatura(properties.segredo()))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(LoginResponse.de(autenticarUsuarioUseCase.autenticarUsuario(request.paraCommand())));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(LoginResponse.de(renovarSessaoUseCase.renovarSessao(request.paraCommand())));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioLoginResponse> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Jwt jwt = jwtDecoder.decode(authorization.substring("Bearer ".length()));
            return ResponseEntity.ok(new UsuarioLoginResponse(
                    UUID.fromString(jwt.getSubject()),
                    empresaId(jwt),
                    jwt.getClaimAsString("nome"),
                    jwt.getClaimAsString("email"),
                    perfis(jwt.getClaimAsStringList("perfis")),
                    authorities(jwt.getClaimAsStringList("authorities"))
            ));
        } catch (JwtException | IllegalArgumentException ignored) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<SolicitarRecuperacaoSenhaResponse> solicitarRecuperacaoSenha(
            @Valid @RequestBody SolicitarRecuperacaoSenhaRequest request
    ) {
        return ResponseEntity.accepted()
                .body(SolicitarRecuperacaoSenhaResponse.de(
                        solicitarRecuperacaoSenhaUseCase.solicitarRecuperacaoSenha(request.paraCommand())
                ));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        redefinirSenhaUseCase.redefinirSenha(request.paraCommand());
        return ResponseEntity.noContent().build();
    }

    private UUID empresaId(Jwt jwt) {
        String empresaId = jwt.getClaimAsString("empresaId");
        return empresaId == null || empresaId.isBlank() ? null : UUID.fromString(empresaId);
    }

    private Set<PerfilAcesso> perfis(List<String> perfis) {
        if (perfis == null) {
            return Set.of();
        }
        return perfis.stream().map(PerfilAcesso::valueOf).collect(Collectors.toUnmodifiableSet());
    }

    private Set<String> authorities(List<String> authorities) {
        if (authorities == null) {
            return Set.of();
        }
        return Set.copyOf(authorities);
    }
}
