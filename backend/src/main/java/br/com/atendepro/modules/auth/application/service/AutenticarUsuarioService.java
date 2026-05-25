package br.com.atendepro.modules.auth.application.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Clock;

import br.com.atendepro.modules.auth.application.command.AutenticarUsuarioCommand;
import br.com.atendepro.modules.auth.application.port.in.AutenticarUsuarioUseCase;
import br.com.atendepro.modules.auth.application.port.out.CarregarUsuarioPorEmailPort;
import br.com.atendepro.modules.auth.application.port.out.GerarRefreshTokenPort;
import br.com.atendepro.modules.auth.application.port.out.GerarTokenAutenticacaoPort;
import br.com.atendepro.modules.auth.application.port.out.SalvarRefreshTokenPort;
import br.com.atendepro.modules.auth.application.port.out.VerificarSenhaPort;
import br.com.atendepro.modules.auth.application.result.AutenticacaoResult;
import br.com.atendepro.modules.auth.domain.exception.AutenticacaoException;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

@Service
@Profile("!test")
public class AutenticarUsuarioService implements AutenticarUsuarioUseCase {

    private final CarregarUsuarioPorEmailPort carregarUsuarioPorEmailPort;
    private final VerificarSenhaPort verificarSenhaPort;
    private final GerarTokenAutenticacaoPort gerarTokenAutenticacaoPort;
    private final GerarRefreshTokenPort gerarRefreshTokenPort;
    private final SalvarRefreshTokenPort salvarRefreshTokenPort;
    private final Clock clock;

    public AutenticarUsuarioService(
            CarregarUsuarioPorEmailPort carregarUsuarioPorEmailPort,
            VerificarSenhaPort verificarSenhaPort,
            GerarTokenAutenticacaoPort gerarTokenAutenticacaoPort,
            GerarRefreshTokenPort gerarRefreshTokenPort,
            SalvarRefreshTokenPort salvarRefreshTokenPort,
            Clock clock
    ) {
        this.carregarUsuarioPorEmailPort = carregarUsuarioPorEmailPort;
        this.verificarSenhaPort = verificarSenhaPort;
        this.gerarTokenAutenticacaoPort = gerarTokenAutenticacaoPort;
        this.gerarRefreshTokenPort = gerarRefreshTokenPort;
        this.salvarRefreshTokenPort = salvarRefreshTokenPort;
        this.clock = clock;
    }

    @Override
    public AutenticacaoResult autenticarUsuario(AutenticarUsuarioCommand command) {
        UsuarioAutenticacao usuario = carregarUsuarioPorEmailPort.carregarUsuarioPorEmail(command.email())
                .orElseThrow(() -> credenciaisInvalidas());

        if (!usuario.ativo()) {
            throw new AutenticacaoException("AUTH_USUARIO_INATIVO", "Usuario inativo.");
        }
        if (!verificarSenhaPort.senhaConfere(command.senha(), usuario.senhaHash())) {
            throw credenciaisInvalidas();
        }

        return RefreshTokenResultFactory.criarAutenticacaoComRefresh(
                usuario,
                gerarTokenAutenticacaoPort,
                gerarRefreshTokenPort,
                salvarRefreshTokenPort,
                clock
        );
    }

    private AutenticacaoException credenciaisInvalidas() {
        return new AutenticacaoException("AUTH_CREDENCIAIS_INVALIDAS", "Email ou senha invalidos.");
    }
}
