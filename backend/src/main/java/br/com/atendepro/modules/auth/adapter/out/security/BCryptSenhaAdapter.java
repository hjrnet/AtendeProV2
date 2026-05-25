package br.com.atendepro.modules.auth.adapter.out.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.atendepro.modules.auth.application.port.out.CriptografarSenhaPort;
import br.com.atendepro.modules.auth.application.port.out.VerificarSenhaPort;

@Component
public class BCryptSenhaAdapter implements CriptografarSenhaPort, VerificarSenhaPort {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Override
    public String criptografarSenha(String senhaEmTexto) {
        return passwordEncoder.encode(senhaEmTexto);
    }

    @Override
    public boolean senhaConfere(String senhaEmTexto, String senhaHash) {
        return passwordEncoder.matches(senhaEmTexto, senhaHash);
    }
}
