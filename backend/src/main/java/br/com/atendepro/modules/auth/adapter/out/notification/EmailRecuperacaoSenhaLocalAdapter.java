package br.com.atendepro.modules.auth.adapter.out.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.com.atendepro.modules.auth.application.port.out.EnviarTokenRecuperacaoSenhaPort;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

@Component
@Profile("!test")
public class EmailRecuperacaoSenhaLocalAdapter implements EnviarTokenRecuperacaoSenhaPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailRecuperacaoSenhaLocalAdapter.class);

    @Override
    public void enviarTokenRecuperacaoSenha(UsuarioAutenticacao usuario, String tokenEmTexto) {
        LOGGER.info("Token local de recuperacao de senha gerado para {}", usuario.email().valor());
    }
}
