package br.com.atendepro.modules.suporte.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.suporte.application.command.AbrirChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.application.command.AtualizarTriagemChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.application.command.RegistrarMensagemChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.application.port.in.AbrirChamadoSuporteUseCase;
import br.com.atendepro.modules.suporte.application.port.in.AtualizarTriagemChamadoSuporteUseCase;
import br.com.atendepro.modules.suporte.application.port.in.DetalharChamadoSuporteUseCase;
import br.com.atendepro.modules.suporte.application.port.in.ListarChamadosSuporteUseCase;
import br.com.atendepro.modules.suporte.application.port.in.RegistrarMensagemChamadoSuporteUseCase;
import br.com.atendepro.modules.suporte.application.port.out.AtualizarChamadoSuportePort;
import br.com.atendepro.modules.suporte.application.port.out.CarregarChamadoSuportePorIdPort;
import br.com.atendepro.modules.suporte.application.port.out.ListarChamadosSuportePort;
import br.com.atendepro.modules.suporte.application.port.out.ListarMensagensChamadoSuportePort;
import br.com.atendepro.modules.suporte.application.port.out.SalvarChamadoSuportePort;
import br.com.atendepro.modules.suporte.application.port.out.SalvarMensagemChamadoSuportePort;
import br.com.atendepro.modules.suporte.application.result.ChamadoSuporteResult;
import br.com.atendepro.modules.suporte.application.result.DetalheChamadoSuporteResult;
import br.com.atendepro.modules.suporte.domain.model.ChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.OrigemMensagemChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class ChamadoSuporteService implements
        AbrirChamadoSuporteUseCase,
        AtualizarTriagemChamadoSuporteUseCase,
        DetalharChamadoSuporteUseCase,
        ListarChamadosSuporteUseCase,
        RegistrarMensagemChamadoSuporteUseCase {

    private final SalvarChamadoSuportePort salvarChamadoSuportePort;
    private final AtualizarChamadoSuportePort atualizarChamadoSuportePort;
    private final CarregarChamadoSuportePorIdPort carregarChamadoSuportePorIdPort;
    private final ListarChamadosSuportePort listarChamadosSuportePort;
    private final SalvarMensagemChamadoSuportePort salvarMensagemChamadoSuportePort;
    private final ListarMensagensChamadoSuportePort listarMensagensChamadoSuportePort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public ChamadoSuporteService(
            SalvarChamadoSuportePort salvarChamadoSuportePort,
            AtualizarChamadoSuportePort atualizarChamadoSuportePort,
            CarregarChamadoSuportePorIdPort carregarChamadoSuportePorIdPort,
            ListarChamadosSuportePort listarChamadosSuportePort,
            SalvarMensagemChamadoSuportePort salvarMensagemChamadoSuportePort,
            ListarMensagensChamadoSuportePort listarMensagensChamadoSuportePort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarChamadoSuportePort = salvarChamadoSuportePort;
        this.atualizarChamadoSuportePort = atualizarChamadoSuportePort;
        this.carregarChamadoSuportePorIdPort = carregarChamadoSuportePorIdPort;
        this.listarChamadosSuportePort = listarChamadosSuportePort;
        this.salvarMensagemChamadoSuportePort = salvarMensagemChamadoSuportePort;
        this.listarMensagensChamadoSuportePort = listarMensagensChamadoSuportePort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public DetalheChamadoSuporteResult abrirChamado(AbrirChamadoSuporteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        Instant agora = Instant.now(clock);
        ChamadoSuporte chamado = ChamadoSuporte.abrir(
                empresaId,
                command.solicitanteUsuarioId(),
                command.solicitanteNome(),
                command.solicitanteEmail(),
                command.titulo(),
                command.descricao(),
                command.prioridade(),
                command.categoria(),
                agora
        );
        MensagemChamadoSuporte mensagemInicial = MensagemChamadoSuporte.registrar(
                chamado.id(),
                command.solicitanteUsuarioId(),
                command.solicitanteNome(),
                OrigemMensagemChamadoSuporte.CLIENTE,
                command.descricao(),
                agora
        );
        salvarChamadoSuportePort.salvarChamado(chamado);
        salvarMensagemChamadoSuportePort.salvarMensagem(mensagemInicial);
        return DetalheChamadoSuporteResult.de(chamado, listarMensagensChamadoSuportePort.listarMensagens(chamado.id()));
    }

    @Override
    public Optional<DetalheChamadoSuporteResult> detalharChamado(UUID chamadoId) {
        validarPermissao();
        return carregarChamadoSuportePorIdPort.carregarChamadoPorId(chamadoId)
                .filter(chamado -> {
                    tenantAccessService.validarAcessoEmpresa(chamado.empresaId());
                    return true;
                })
                .map(chamado -> DetalheChamadoSuporteResult.de(chamado, listarMensagensChamadoSuportePort.listarMensagens(chamado.id())));
    }

    @Override
    public ResultadoPaginado<ChamadoSuporteResult> listarChamados(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            StatusChamadoSuporte status,
            PrioridadeChamadoSuporte prioridade
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        var chamados = listarChamadosSuportePort.listarChamados(empresaResolvida, paginacao, busca, status, prioridade);
        return new ResultadoPaginado<>(
                chamados.itens().stream().map(ChamadoSuporteResult::de).toList(),
                chamados.totalItens(),
                chamados.pagina(),
                chamados.tamanho()
        );
    }

    @Override
    public DetalheChamadoSuporteResult registrarMensagem(RegistrarMensagemChamadoSuporteCommand command) {
        validarPermissao();
        ChamadoSuporte chamado = carregarChamadoSuportePorIdPort.carregarChamadoPorId(command.chamadoId())
                .orElseThrow(() -> new BusinessException("SUPORTE_CHAMADO_NAO_ENCONTRADO", "Chamado de suporte nao encontrado."));
        tenantAccessService.validarAcessoEmpresa(chamado.empresaId());
        Instant agora = Instant.now(clock);
        MensagemChamadoSuporte mensagem = MensagemChamadoSuporte.registrar(
                chamado.id(),
                command.autorUsuarioId(),
                command.autorNome(),
                command.origem() == null ? OrigemMensagemChamadoSuporte.CLIENTE : command.origem(),
                command.mensagem(),
                agora
        );
        salvarMensagemChamadoSuportePort.salvarMensagem(mensagem);
        atualizarChamadoSuportePort.atualizarChamado(chamado.marcarAtualizado(agora));
        return DetalheChamadoSuporteResult.de(chamado.marcarAtualizado(agora), listarMensagensChamadoSuportePort.listarMensagens(chamado.id()));
    }

    @Override
    public DetalheChamadoSuporteResult atualizarTriagem(AtualizarTriagemChamadoSuporteCommand command) {
        validarPermissao();
        ChamadoSuporte chamado = carregarChamadoSuportePorIdPort.carregarChamadoPorId(command.chamadoId())
                .orElseThrow(() -> new BusinessException("SUPORTE_CHAMADO_NAO_ENCONTRADO", "Chamado de suporte nao encontrado."));
        tenantAccessService.validarAcessoEmpresa(chamado.empresaId());
        ChamadoSuporte chamadoAtualizado = chamado.alterarTriagem(command.status(), command.prioridade(), Instant.now(clock));
        atualizarChamadoSuportePort.atualizarChamado(chamadoAtualizado);
        return DetalheChamadoSuporteResult.de(
                chamadoAtualizado,
                listarMensagensChamadoSuportePort.listarMensagens(chamado.id())
        );
    }

    private UUID resolverEmpresaId(UUID empresaIdSolicitada) {
        Optional<UUID> empresaRestrita = tenantAccessService.empresaRestrita();
        if (empresaRestrita.isPresent()) {
            if (empresaIdSolicitada != null) {
                tenantAccessService.validarAcessoEmpresa(empresaIdSolicitada);
            }
            return empresaRestrita.get();
        }
        if (empresaIdSolicitada == null) {
            throw new BusinessException("SUPORTE_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar chamados de suporte.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_CHAMADOS);
    }
}
