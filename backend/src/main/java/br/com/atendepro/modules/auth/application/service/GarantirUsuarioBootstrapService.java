package br.com.atendepro.modules.auth.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.command.CadastrarUsuarioBootstrapCommand;
import br.com.atendepro.modules.auth.application.port.in.GarantirUsuarioBootstrapUseCase;
import br.com.atendepro.modules.auth.application.port.out.CarregarUsuarioPorEmailPort;
import br.com.atendepro.modules.auth.application.port.out.CriptografarSenhaPort;
import br.com.atendepro.modules.auth.application.port.out.SalvarUsuarioAutenticacaoPort;
import br.com.atendepro.modules.auth.application.result.UsuarioBootstrapResult;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

@Service
@Profile("!test")
public class GarantirUsuarioBootstrapService implements GarantirUsuarioBootstrapUseCase {

    private final CarregarUsuarioPorEmailPort carregarUsuarioPorEmailPort;
    private final SalvarUsuarioAutenticacaoPort salvarUsuarioAutenticacaoPort;
    private final CriptografarSenhaPort criptografarSenhaPort;
    private final Clock clock;

    public GarantirUsuarioBootstrapService(
            CarregarUsuarioPorEmailPort carregarUsuarioPorEmailPort,
            SalvarUsuarioAutenticacaoPort salvarUsuarioAutenticacaoPort,
            CriptografarSenhaPort criptografarSenhaPort,
            Clock clock
    ) {
        this.carregarUsuarioPorEmailPort = carregarUsuarioPorEmailPort;
        this.salvarUsuarioAutenticacaoPort = salvarUsuarioAutenticacaoPort;
        this.criptografarSenhaPort = criptografarSenhaPort;
        this.clock = clock;
    }

    @Override
    public UsuarioBootstrapResult garantirUsuarioBootstrap(CadastrarUsuarioBootstrapCommand command) {
        return carregarUsuarioPorEmailPort.carregarUsuarioPorEmail(command.email())
                .map(usuarioExistente -> criarResult(usuarioExistente, false))
                .orElseGet(() -> cadastrarUsuarioBootstrap(command));
    }

    private UsuarioBootstrapResult cadastrarUsuarioBootstrap(CadastrarUsuarioBootstrapCommand command) {
        UsuarioAutenticacao usuario = new UsuarioAutenticacao(
                UUID.randomUUID(),
                command.email(),
                command.nome().trim(),
                criptografarSenhaPort.criptografarSenha(command.senhaEmTexto()),
                command.perfis(),
                true,
                Instant.now(clock)
        );
        salvarUsuarioAutenticacaoPort.salvarUsuarioAutenticacao(usuario);
        return criarResult(usuario, true);
    }

    private UsuarioBootstrapResult criarResult(UsuarioAutenticacao usuario, boolean criado) {
        return new UsuarioBootstrapResult(
                usuario.id(),
                usuario.nome(),
                usuario.email().valor(),
                usuario.perfis(),
                criado
        );
    }
}
