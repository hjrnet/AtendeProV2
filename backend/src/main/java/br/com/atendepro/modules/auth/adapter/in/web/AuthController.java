package br.com.atendepro.modules.auth.adapter.in.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.auth.application.port.in.AutenticarUsuarioUseCase;
import br.com.atendepro.modules.auth.application.port.in.RedefinirSenhaUseCase;
import br.com.atendepro.modules.auth.application.port.in.RenovarSessaoUseCase;
import br.com.atendepro.modules.auth.application.port.in.SolicitarRecuperacaoSenhaUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Profile("!test")
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final RenovarSessaoUseCase renovarSessaoUseCase;
    private final SolicitarRecuperacaoSenhaUseCase solicitarRecuperacaoSenhaUseCase;
    private final RedefinirSenhaUseCase redefinirSenhaUseCase;

    public AuthController(
            AutenticarUsuarioUseCase autenticarUsuarioUseCase,
            RenovarSessaoUseCase renovarSessaoUseCase,
            SolicitarRecuperacaoSenhaUseCase solicitarRecuperacaoSenhaUseCase,
            RedefinirSenhaUseCase redefinirSenhaUseCase
    ) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.renovarSessaoUseCase = renovarSessaoUseCase;
        this.solicitarRecuperacaoSenhaUseCase = solicitarRecuperacaoSenhaUseCase;
        this.redefinirSenhaUseCase = redefinirSenhaUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(LoginResponse.de(autenticarUsuarioUseCase.autenticarUsuario(request.paraCommand())));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(LoginResponse.de(renovarSessaoUseCase.renovarSessao(request.paraCommand())));
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
}
