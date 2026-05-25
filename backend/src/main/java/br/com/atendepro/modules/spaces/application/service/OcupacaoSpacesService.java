package br.com.atendepro.modules.spaces.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.spaces.application.command.AgendarOcupacaoSpacesCommand;
import br.com.atendepro.modules.spaces.application.command.ConsultarDisponibilidadeSpacesCommand;
import br.com.atendepro.modules.spaces.application.port.in.AgendarOcupacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ConsultarDisponibilidadeSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.DetalharOcupacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ListarOcupacoesSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.out.CarregarOcupacaoSpacesPorIdPort;
import br.com.atendepro.modules.spaces.application.port.out.CarregarPacoteSublocacaoSpacesPorIdPort;
import br.com.atendepro.modules.spaces.application.port.out.CarregarRecursoSpacesPorIdPort;
import br.com.atendepro.modules.spaces.application.port.out.ListarOcupacoesSpacesPort;
import br.com.atendepro.modules.spaces.application.port.out.SalvarOcupacaoSpacesPort;
import br.com.atendepro.modules.spaces.application.port.out.VerificarConflitoOcupacaoSpacesPort;
import br.com.atendepro.modules.spaces.application.result.DisponibilidadeSpacesResult;
import br.com.atendepro.modules.spaces.application.result.OcupacaoSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.OcupacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.PacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.RecursoSpaces;
import br.com.atendepro.modules.spaces.domain.model.StatusOcupacaoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class OcupacaoSpacesService implements
        AgendarOcupacaoSpacesUseCase,
        ConsultarDisponibilidadeSpacesUseCase,
        DetalharOcupacaoSpacesUseCase,
        ListarOcupacoesSpacesUseCase {

    private final SalvarOcupacaoSpacesPort salvarOcupacaoSpacesPort;
    private final CarregarOcupacaoSpacesPorIdPort carregarOcupacaoSpacesPorIdPort;
    private final ListarOcupacoesSpacesPort listarOcupacoesSpacesPort;
    private final VerificarConflitoOcupacaoSpacesPort verificarConflitoOcupacaoSpacesPort;
    private final CarregarRecursoSpacesPorIdPort carregarRecursoSpacesPorIdPort;
    private final CarregarPacoteSublocacaoSpacesPorIdPort carregarPacoteSublocacaoSpacesPorIdPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public OcupacaoSpacesService(
            SalvarOcupacaoSpacesPort salvarOcupacaoSpacesPort,
            CarregarOcupacaoSpacesPorIdPort carregarOcupacaoSpacesPorIdPort,
            ListarOcupacoesSpacesPort listarOcupacoesSpacesPort,
            VerificarConflitoOcupacaoSpacesPort verificarConflitoOcupacaoSpacesPort,
            CarregarRecursoSpacesPorIdPort carregarRecursoSpacesPorIdPort,
            CarregarPacoteSublocacaoSpacesPorIdPort carregarPacoteSublocacaoSpacesPorIdPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarOcupacaoSpacesPort = salvarOcupacaoSpacesPort;
        this.carregarOcupacaoSpacesPorIdPort = carregarOcupacaoSpacesPorIdPort;
        this.listarOcupacoesSpacesPort = listarOcupacoesSpacesPort;
        this.verificarConflitoOcupacaoSpacesPort = verificarConflitoOcupacaoSpacesPort;
        this.carregarRecursoSpacesPorIdPort = carregarRecursoSpacesPorIdPort;
        this.carregarPacoteSublocacaoSpacesPorIdPort = carregarPacoteSublocacaoSpacesPorIdPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public OcupacaoSpacesResult agendarOcupacao(AgendarOcupacaoSpacesCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarRecurso(command.recursoId(), empresaId);
        validarPacote(command.pacoteId(), empresaId, command.recursoId());
        if (verificarConflitoOcupacaoSpacesPort.existeConflitoOcupacao(
                empresaId,
                command.recursoId(),
                command.inicioEm(),
                command.fimEm()
        )) {
            throw new BusinessException("SPACES_OCUPACAO_CONFLITO", "Recurso Spaces ja possui ocupacao no periodo informado.");
        }
        OcupacaoSpaces ocupacao = OcupacaoSpaces.agendar(
                empresaId,
                command.recursoId(),
                command.pacoteId(),
                command.nomeParceiro(),
                command.inicioEm(),
                command.fimEm(),
                command.status(),
                command.observacao(),
                Instant.now(clock)
        );
        salvarOcupacaoSpacesPort.salvarOcupacao(ocupacao);
        return OcupacaoSpacesResult.de(ocupacao);
    }

    @Override
    public DisponibilidadeSpacesResult consultarDisponibilidade(ConsultarDisponibilidadeSpacesCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarRecurso(command.recursoId(), empresaId);
        if (command.inicioEm() == null || command.fimEm() == null || !command.fimEm().isAfter(command.inicioEm())) {
            throw new BusinessException("SPACES_PERIODO_INVALIDO", "Periodo da disponibilidade Spaces e invalido.");
        }
        boolean ocupado = verificarConflitoOcupacaoSpacesPort.existeConflitoOcupacao(
                empresaId,
                command.recursoId(),
                command.inicioEm(),
                command.fimEm()
        );
        return new DisponibilidadeSpacesResult(
                empresaId,
                command.recursoId(),
                command.inicioEm(),
                command.fimEm(),
                !ocupado,
                ocupado ? "Recurso ocupado no periodo informado." : "Recurso disponivel no periodo informado."
        );
    }

    @Override
    public Optional<OcupacaoSpacesResult> detalharOcupacao(UUID ocupacaoId) {
        validarPermissao();
        return carregarOcupacaoSpacesPorIdPort.carregarOcupacaoPorId(ocupacaoId)
                .filter(ocupacao -> {
                    tenantAccessService.validarAcessoEmpresa(ocupacao.empresaId());
                    return true;
                })
                .map(OcupacaoSpacesResult::de);
    }

    @Override
    public ResultadoPaginado<OcupacaoSpacesResult> listarOcupacoes(
            UUID empresaId,
            Paginacao paginacao,
            UUID recursoId,
            Instant inicioEm,
            Instant fimEm,
            StatusOcupacaoSpaces status
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        if (recursoId != null) {
            validarRecurso(recursoId, empresaResolvida);
        }
        var ocupacoes = listarOcupacoesSpacesPort.listarOcupacoes(empresaResolvida, paginacao, recursoId, inicioEm, fimEm, status);
        return new ResultadoPaginado<>(
                ocupacoes.itens().stream().map(OcupacaoSpacesResult::de).toList(),
                ocupacoes.totalItens(),
                ocupacoes.pagina(),
                ocupacoes.tamanho()
        );
    }

    private void validarRecurso(UUID recursoId, UUID empresaId) {
        RecursoSpaces recurso = carregarRecursoSpacesPorIdPort.carregarRecursoPorId(recursoId)
                .orElseThrow(() -> new BusinessException("SPACES_RECURSO_NAO_ENCONTRADO", "Recurso Spaces nao encontrado."));
        tenantAccessService.validarAcessoEmpresa(recurso.empresaId());
        if (!recurso.empresaId().equals(empresaId)) {
            throw new BusinessException("SPACES_RECURSO_EMPRESA_DIVERGENTE", "Recurso Spaces nao pertence a empresa informada.");
        }
    }

    private void validarPacote(UUID pacoteId, UUID empresaId, UUID recursoId) {
        if (pacoteId == null) {
            return;
        }
        PacoteSublocacaoSpaces pacote = carregarPacoteSublocacaoSpacesPorIdPort.carregarPacotePorId(pacoteId)
                .orElseThrow(() -> new BusinessException("SPACES_PACOTE_NAO_ENCONTRADO", "Pacote de sublocacao nao encontrado."));
        tenantAccessService.validarAcessoEmpresa(pacote.empresaId());
        if (!pacote.empresaId().equals(empresaId)) {
            throw new BusinessException("SPACES_PACOTE_EMPRESA_DIVERGENTE", "Pacote de sublocacao nao pertence a empresa informada.");
        }
        if (pacote.recursoId() != null && !pacote.recursoId().equals(recursoId)) {
            throw new BusinessException("SPACES_PACOTE_RECURSO_DIVERGENTE", "Pacote de sublocacao nao pertence ao recurso informado.");
        }
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
            throw new BusinessException("SPACES_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar Spaces.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_SPACES);
    }
}
