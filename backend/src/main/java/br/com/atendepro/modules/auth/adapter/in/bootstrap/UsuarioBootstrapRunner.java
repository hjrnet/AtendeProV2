package br.com.atendepro.modules.auth.adapter.in.bootstrap;

import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.com.atendepro.modules.auth.application.command.CadastrarUsuarioBootstrapCommand;
import br.com.atendepro.modules.auth.application.port.in.GarantirUsuarioBootstrapUseCase;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;

@Component
@Profile("local")
public class UsuarioBootstrapRunner implements ApplicationRunner {

    private final UsuarioBootstrapProperties properties;
    private final GarantirUsuarioBootstrapUseCase garantirUsuarioBootstrapUseCase;

    public UsuarioBootstrapRunner(
            UsuarioBootstrapProperties properties,
            GarantirUsuarioBootstrapUseCase garantirUsuarioBootstrapUseCase
    ) {
        this.properties = properties;
        this.garantirUsuarioBootstrapUseCase = garantirUsuarioBootstrapUseCase;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.habilitado()) {
            return;
        }
        garantirUsuarioBootstrapUseCase.garantirUsuarioBootstrap(new CadastrarUsuarioBootstrapCommand(
                properties.nomeConfigurado(),
                EmailUsuario.de(properties.emailConfigurado()),
                properties.senhaConfigurada(),
                Set.of(PerfilAcesso.SUPER_ADMIN)
        ));
    }
}
