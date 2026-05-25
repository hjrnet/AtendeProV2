package br.com.atendepro.modules.auth.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.adapter.out.security.JwtAutenticacaoProperties;
import br.com.atendepro.modules.auth.application.command.SolicitarRecuperacaoSenhaCommand;
import br.com.atendepro.modules.auth.application.port.in.SolicitarRecuperacaoSenhaUseCase;
import br.com.atendepro.modules.auth.application.port.out.CarregarUsuarioPorEmailPort;
import br.com.atendepro.modules.auth.application.port.out.EnviarTokenRecuperacaoSenhaPort;
import br.com.atendepro.modules.auth.application.port.out.GerarTokenRecuperacaoSenhaPort;
import br.com.atendepro.modules.auth.application.port.out.SalvarTokenRecuperacaoSenhaPort;
import br.com.atendepro.modules.auth.application.port.out.TokenRecuperacaoSenhaGerado;
import br.com.atendepro.modules.auth.application.result.SolicitarRecuperacaoSenhaResult;
import br.com.atendepro.modules.auth.domain.model.TokenRecuperacaoSenha;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

@Service
@Profile("!test")
public class SolicitarRecuperacaoSenhaService implements SolicitarRecuperacaoSenhaUseCase {

    private final CarregarUsuarioPorEmailPort carregarUsuarioPorEmailPort;
    private final GerarTokenRecuperacaoSenhaPort gerarTokenRecuperacaoSenhaPort;
    private final SalvarTokenRecuperacaoSenhaPort salvarTokenRecuperacaoSenhaPort;
    private final EnviarTokenRecuperacaoSenhaPort enviarTokenRecuperacaoSenhaPort;
    private final JwtAutenticacaoProperties properties;
    private final Clock clock;

    public SolicitarRecuperacaoSenhaService(
            CarregarUsuarioPorEmailPort carregarUsuarioPorEmailPort,
            GerarTokenRecuperacaoSenhaPort gerarTokenRecuperacaoSenhaPort,
            SalvarTokenRecuperacaoSenhaPort salvarTokenRecuperacaoSenhaPort,
            EnviarTokenRecuperacaoSenhaPort enviarTokenRecuperacaoSenhaPort,
            JwtAutenticacaoProperties properties,
            Clock clock
    ) {
        this.carregarUsuarioPorEmailPort = carregarUsuarioPorEmailPort;
        this.gerarTokenRecuperacaoSenhaPort = gerarTokenRecuperacaoSenhaPort;
        this.salvarTokenRecuperacaoSenhaPort = salvarTokenRecuperacaoSenhaPort;
        this.enviarTokenRecuperacaoSenhaPort = enviarTokenRecuperacaoSenhaPort;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    public SolicitarRecuperacaoSenhaResult solicitarRecuperacaoSenha(SolicitarRecuperacaoSenhaCommand command) {
        return carregarUsuarioPorEmailPort.carregarUsuarioPorEmail(command.email())
                .filter(UsuarioAutenticacao::ativo)
                .map(this::criarTokenRecuperacaoSenha)
                .orElseGet(() -> new SolicitarRecuperacaoSenhaResult(null));
    }

    private SolicitarRecuperacaoSenhaResult criarTokenRecuperacaoSenha(UsuarioAutenticacao usuario) {
        TokenRecuperacaoSenhaGerado tokenGerado = gerarTokenRecuperacaoSenhaPort.gerarTokenRecuperacaoSenha(usuario);
        salvarTokenRecuperacaoSenhaPort.salvarTokenRecuperacaoSenha(new TokenRecuperacaoSenha(
                UUID.randomUUID(),
                usuario.id(),
                tokenGerado.tokenHash(),
                tokenGerado.expiraEm(),
                false,
                Instant.now(clock)
        ));
        enviarTokenRecuperacaoSenhaPort.enviarTokenRecuperacaoSenha(usuario, tokenGerado.valor());
        return new SolicitarRecuperacaoSenhaResult(properties.deveExporTokenRecuperacaoLocal() ? tokenGerado.valor() : null);
    }
}
