package br.com.atendepro.modules.plano.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.plano.application.command.AtualizarPlanoCommand;
import br.com.atendepro.modules.plano.application.command.CriarPlanoCommand;
import br.com.atendepro.modules.plano.application.port.in.AtualizarPlanoUseCase;
import br.com.atendepro.modules.plano.application.port.in.BuscarPlanoUseCase;
import br.com.atendepro.modules.plano.application.port.in.CriarPlanoUseCase;
import br.com.atendepro.modules.plano.application.port.in.ListarPlanosUseCase;
import br.com.atendepro.modules.plano.application.port.out.AtualizarPlanoPort;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorCodigoPort;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorIdPort;
import br.com.atendepro.modules.plano.application.port.out.ListarPlanosPort;
import br.com.atendepro.modules.plano.application.port.out.SalvarPlanoPort;
import br.com.atendepro.modules.plano.application.result.PlanoResult;
import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class PlanoService implements CriarPlanoUseCase, AtualizarPlanoUseCase, BuscarPlanoUseCase, ListarPlanosUseCase {

    private final PermissaoAcessoService permissaoAcessoService;
    private final SalvarPlanoPort salvarPlanoPort;
    private final AtualizarPlanoPort atualizarPlanoPort;
    private final CarregarPlanoPorIdPort carregarPlanoPorIdPort;
    private final CarregarPlanoPorCodigoPort carregarPlanoPorCodigoPort;
    private final ListarPlanosPort listarPlanosPort;
    private final Clock clock;

    public PlanoService(
            PermissaoAcessoService permissaoAcessoService,
            SalvarPlanoPort salvarPlanoPort,
            AtualizarPlanoPort atualizarPlanoPort,
            CarregarPlanoPorIdPort carregarPlanoPorIdPort,
            CarregarPlanoPorCodigoPort carregarPlanoPorCodigoPort,
            ListarPlanosPort listarPlanosPort,
            Clock clock
    ) {
        this.permissaoAcessoService = permissaoAcessoService;
        this.salvarPlanoPort = salvarPlanoPort;
        this.atualizarPlanoPort = atualizarPlanoPort;
        this.carregarPlanoPorIdPort = carregarPlanoPorIdPort;
        this.carregarPlanoPorCodigoPort = carregarPlanoPorCodigoPort;
        this.listarPlanosPort = listarPlanosPort;
        this.clock = clock;
    }

    @Override
    public PlanoResult criarPlano(CriarPlanoCommand command) {
        validarAcessoAdminSaas();
        Instant agora = Instant.now(clock);
        var plano = montarPlano(UUID.randomUUID(), command, agora, agora);
        validarCodigoDisponivel(plano.codigo(), null);
        salvarPlanoPort.salvarPlano(plano);
        return PlanoResult.de(plano);
    }

    @Override
    public Optional<PlanoResult> atualizarPlano(AtualizarPlanoCommand command) {
        validarAcessoAdminSaas();
        return carregarPlanoPorIdPort.carregarPlanoPorId(command.id())
                .map(planoAtual -> {
                    var plano = new PlanoAssinatura(
                            planoAtual.id(),
                            command.codigo(),
                            command.nome(),
                            command.descricao(),
                            command.valorMensal(),
                            command.limiteUsuarios(),
                            command.limiteClientes(),
                            command.limiteProfissionais(),
                            command.ativo(),
                            command.estudante(),
                            command.marcaDaguaAcademica(),
                            command.modulos(),
                            planoAtual.criadoEm(),
                            Instant.now(clock)
                    );
                    validarCodigoDisponivel(plano.codigo(), plano.id());
                    atualizarPlanoPort.atualizarPlano(plano);
                    return PlanoResult.de(plano);
                });
    }

    @Override
    public Optional<PlanoResult> buscarPlanoPorId(UUID planoId) {
        validarAcessoAdminSaas();
        return carregarPlanoPorIdPort.carregarPlanoPorId(planoId).map(PlanoResult::de);
    }

    @Override
    public ResultadoPaginado<PlanoResult> listarPlanos(Paginacao paginacao, String busca, Boolean ativo) {
        validarAcessoAdminSaas();
        var planos = listarPlanosPort.listarPlanos(paginacao, busca, ativo);
        return new ResultadoPaginado<>(
                planos.itens().stream().map(PlanoResult::de).toList(),
                planos.totalItens(),
                planos.pagina(),
                planos.tamanho()
        );
    }

    private PlanoAssinatura montarPlano(UUID id, CriarPlanoCommand command, Instant criadoEm, Instant atualizadoEm) {
        return new PlanoAssinatura(
                id,
                command.codigo(),
                command.nome(),
                command.descricao(),
                command.valorMensal(),
                command.limiteUsuarios(),
                command.limiteClientes(),
                command.limiteProfissionais(),
                command.ativo(),
                command.estudante(),
                command.marcaDaguaAcademica(),
                command.modulos(),
                criadoEm,
                atualizadoEm
        );
    }

    private void validarCodigoDisponivel(String codigo, UUID planoAtualId) {
        carregarPlanoPorCodigoPort.carregarPlanoPorCodigo(codigo)
                .filter(plano -> !plano.id().equals(planoAtualId))
                .ifPresent(plano -> {
                    throw new BusinessException("PLANO_CODIGO_JA_CADASTRADO", "Codigo de plano ja cadastrado.");
                });
    }

    private void validarAcessoAdminSaas() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
    }
}
