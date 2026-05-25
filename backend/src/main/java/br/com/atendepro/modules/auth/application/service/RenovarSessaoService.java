package br.com.atendepro.modules.auth.application.service;

import java.time.Clock;
import java.time.Instant;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.command.RenovarSessaoCommand;
import br.com.atendepro.modules.auth.application.port.in.RenovarSessaoUseCase;
import br.com.atendepro.modules.auth.application.port.out.CarregarRefreshTokenAtivoPort;
import br.com.atendepro.modules.auth.application.port.out.CarregarUsuarioPorIdPort;
import br.com.atendepro.modules.auth.application.port.out.GerarRefreshTokenPort;
import br.com.atendepro.modules.auth.application.port.out.GerarTokenAutenticacaoPort;
import br.com.atendepro.modules.auth.application.port.out.HashRefreshTokenPort;
import br.com.atendepro.modules.auth.application.port.out.RevogarRefreshTokenPort;
import br.com.atendepro.modules.auth.application.port.out.SalvarRefreshTokenPort;
import br.com.atendepro.modules.auth.application.result.AutenticacaoResult;
import br.com.atendepro.modules.auth.domain.exception.AutenticacaoException;
import br.com.atendepro.modules.auth.domain.model.RefreshTokenAutenticacao;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

@Service
@Profile("!test")
public class RenovarSessaoService implements RenovarSessaoUseCase {

    private final HashRefreshTokenPort hashRefreshTokenPort;
    private final CarregarRefreshTokenAtivoPort carregarRefreshTokenAtivoPort;
    private final CarregarUsuarioPorIdPort carregarUsuarioPorIdPort;
    private final RevogarRefreshTokenPort revogarRefreshTokenPort;
    private final GerarTokenAutenticacaoPort gerarTokenAutenticacaoPort;
    private final GerarRefreshTokenPort gerarRefreshTokenPort;
    private final SalvarRefreshTokenPort salvarRefreshTokenPort;
    private final Clock clock;

    public RenovarSessaoService(
            HashRefreshTokenPort hashRefreshTokenPort,
            CarregarRefreshTokenAtivoPort carregarRefreshTokenAtivoPort,
            CarregarUsuarioPorIdPort carregarUsuarioPorIdPort,
            RevogarRefreshTokenPort revogarRefreshTokenPort,
            GerarTokenAutenticacaoPort gerarTokenAutenticacaoPort,
            GerarRefreshTokenPort gerarRefreshTokenPort,
            SalvarRefreshTokenPort salvarRefreshTokenPort,
            Clock clock
    ) {
        this.hashRefreshTokenPort = hashRefreshTokenPort;
        this.carregarRefreshTokenAtivoPort = carregarRefreshTokenAtivoPort;
        this.carregarUsuarioPorIdPort = carregarUsuarioPorIdPort;
        this.revogarRefreshTokenPort = revogarRefreshTokenPort;
        this.gerarTokenAutenticacaoPort = gerarTokenAutenticacaoPort;
        this.gerarRefreshTokenPort = gerarRefreshTokenPort;
        this.salvarRefreshTokenPort = salvarRefreshTokenPort;
        this.clock = clock;
    }

    @Override
    public AutenticacaoResult renovarSessao(RenovarSessaoCommand command) {
        Instant agora = Instant.now(clock);
        RefreshTokenAutenticacao refreshToken = carregarRefreshTokenAtivoPort
                .carregarRefreshTokenAtivo(hashRefreshTokenPort.gerarHashRefreshToken(command.refreshToken()), agora)
                .orElseThrow(() -> new AutenticacaoException("AUTH_REFRESH_INVALIDO", "Refresh token invalido."));

        UsuarioAutenticacao usuario = carregarUsuarioPorIdPort.carregarUsuarioPorId(refreshToken.usuarioId())
                .orElseThrow(() -> new AutenticacaoException("AUTH_USUARIO_NAO_ENCONTRADO", "Usuario nao encontrado."));
        if (!usuario.ativo()) {
            throw new AutenticacaoException("AUTH_USUARIO_INATIVO", "Usuario inativo.");
        }

        revogarRefreshTokenPort.revogarRefreshToken(refreshToken.id(), agora);
        return RefreshTokenResultFactory.criarAutenticacaoComRefresh(
                usuario,
                gerarTokenAutenticacaoPort,
                gerarRefreshTokenPort,
                salvarRefreshTokenPort,
                clock
        );
    }
}
