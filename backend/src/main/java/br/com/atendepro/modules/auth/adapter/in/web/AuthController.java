package br.com.atendepro.modules.auth.adapter.in.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.auth.application.port.in.AutenticarUsuarioUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Profile("!test")
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(LoginResponse.de(autenticarUsuarioUseCase.autenticarUsuario(request.paraCommand())));
    }
}
