package br.com.atendepro.modules.auth.application.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.command.AutenticarUsuarioCommand;
import br.com.atendepro.modules.auth.application.port.in.AutenticarUsuarioUseCase;
import br.com.atendepro.modules.auth.application.port.out.CarregarUsuarioPorEmailPort;
import br.com.atendepro.modules.auth.application.port.out.GerarTokenAutenticacaoPort;
import br.com.atendepro.modules.auth.application.port.out.TokenGerado;
import br.com.atendepro.modules.auth.application.port.out.VerificarSenhaPort;
import br.com.atendepro.modules.auth.application.result.AutenticacaoResult;
import br.com.atendepro.modules.auth.application.result.UsuarioAutenticadoResult;
import br.com.atendepro.modules.auth.domain.exception.AutenticacaoException;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

@Service
@Profile("!test")
public class AutenticarUsuarioService implements AutenticarUsuarioUseCase {

    private final CarregarUsuarioPorEmailPort carregarUsuarioPorEmailPort;
    private final VerificarSenhaPort verificarSenhaPort;
    private final GerarTokenAutenticacaoPort gerarTokenAutenticacaoPort;

    public AutenticarUsuarioService(
            CarregarUsuarioPorEmailPort carregarUsuarioPorEmailPort,
            VerificarSenhaPort verificarSenhaPort,
            GerarTokenAutenticacaoPort gerarTokenAutenticacaoPort
    ) {
        this.carregarUsuarioPorEmailPort = carregarUsuarioPorEmailPort;
        this.verificarSenhaPort = verificarSenhaPort;
        this.gerarTokenAutenticacaoPort = gerarTokenAutenticacaoPort;
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

        TokenGerado token = gerarTokenAutenticacaoPort.gerarAccessToken(usuario);
        return new AutenticacaoResult(
                token.valor(),
                "Bearer",
                token.expiraEm(),
                new UsuarioAutenticadoResult(
                        usuario.id(),
                        usuario.nome(),
                        usuario.email().valor(),
                        usuario.perfis()
                )
        );
    }

    private AutenticacaoException credenciaisInvalidas() {
        return new AutenticacaoException("AUTH_CREDENCIAIS_INVALIDAS", "Email ou senha invalidos.");
    }
}
