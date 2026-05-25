package br.com.atendepro.modules.auth.application.service;

import java.time.Clock;
import java.time.Instant;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.command.RedefinirSenhaCommand;
import br.com.atendepro.modules.auth.application.port.in.RedefinirSenhaUseCase;
import br.com.atendepro.modules.auth.application.port.out.AtualizarSenhaUsuarioPort;
import br.com.atendepro.modules.auth.application.port.out.CarregarTokenRecuperacaoSenhaAtivoPort;
import br.com.atendepro.modules.auth.application.port.out.CriptografarSenhaPort;
import br.com.atendepro.modules.auth.application.port.out.HashTokenRecuperacaoSenhaPort;
import br.com.atendepro.modules.auth.application.port.out.MarcarTokenRecuperacaoSenhaUtilizadoPort;
import br.com.atendepro.modules.auth.domain.exception.AutenticacaoException;
import br.com.atendepro.modules.auth.domain.model.TokenRecuperacaoSenha;

@Service
@Profile("!test")
public class RedefinirSenhaService implements RedefinirSenhaUseCase {

    private final HashTokenRecuperacaoSenhaPort hashTokenRecuperacaoSenhaPort;
    private final CarregarTokenRecuperacaoSenhaAtivoPort carregarTokenRecuperacaoSenhaAtivoPort;
    private final AtualizarSenhaUsuarioPort atualizarSenhaUsuarioPort;
    private final CriptografarSenhaPort criptografarSenhaPort;
    private final MarcarTokenRecuperacaoSenhaUtilizadoPort marcarTokenRecuperacaoSenhaUtilizadoPort;
    private final Clock clock;

    public RedefinirSenhaService(
            HashTokenRecuperacaoSenhaPort hashTokenRecuperacaoSenhaPort,
            CarregarTokenRecuperacaoSenhaAtivoPort carregarTokenRecuperacaoSenhaAtivoPort,
            AtualizarSenhaUsuarioPort atualizarSenhaUsuarioPort,
            CriptografarSenhaPort criptografarSenhaPort,
            MarcarTokenRecuperacaoSenhaUtilizadoPort marcarTokenRecuperacaoSenhaUtilizadoPort,
            Clock clock
    ) {
        this.hashTokenRecuperacaoSenhaPort = hashTokenRecuperacaoSenhaPort;
        this.carregarTokenRecuperacaoSenhaAtivoPort = carregarTokenRecuperacaoSenhaAtivoPort;
        this.atualizarSenhaUsuarioPort = atualizarSenhaUsuarioPort;
        this.criptografarSenhaPort = criptografarSenhaPort;
        this.marcarTokenRecuperacaoSenhaUtilizadoPort = marcarTokenRecuperacaoSenhaUtilizadoPort;
        this.clock = clock;
    }

    @Override
    public void redefinirSenha(RedefinirSenhaCommand command) {
        Instant agora = Instant.now(clock);
        TokenRecuperacaoSenha token = carregarTokenRecuperacaoSenhaAtivoPort
                .carregarTokenRecuperacaoSenhaAtivo(hashTokenRecuperacaoSenhaPort.gerarHashTokenRecuperacaoSenha(command.token()), agora)
                .orElseThrow(() -> new AutenticacaoException("AUTH_TOKEN_RECUPERACAO_INVALIDO", "Token de recuperacao invalido."));

        atualizarSenhaUsuarioPort.atualizarSenhaUsuario(token.usuarioId(), criptografarSenhaPort.criptografarSenha(command.novaSenha()));
        marcarTokenRecuperacaoSenhaUtilizadoPort.marcarTokenRecuperacaoSenhaUtilizado(token.id(), agora);
    }
}
