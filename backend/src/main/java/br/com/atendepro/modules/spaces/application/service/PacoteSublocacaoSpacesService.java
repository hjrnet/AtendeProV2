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
import br.com.atendepro.modules.spaces.application.command.CadastrarPacoteSublocacaoSpacesCommand;
import br.com.atendepro.modules.spaces.application.port.in.CadastrarPacoteSublocacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.DetalharPacoteSublocacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ListarPacotesSublocacaoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.out.CarregarPacoteSublocacaoSpacesPorIdPort;
import br.com.atendepro.modules.spaces.application.port.out.CarregarRecursoSpacesPorIdPort;
import br.com.atendepro.modules.spaces.application.port.out.ListarPacotesSublocacaoSpacesPort;
import br.com.atendepro.modules.spaces.application.port.out.SalvarPacoteSublocacaoSpacesPort;
import br.com.atendepro.modules.spaces.application.result.PacoteSublocacaoSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.PacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class PacoteSublocacaoSpacesService implements
        CadastrarPacoteSublocacaoSpacesUseCase,
        DetalharPacoteSublocacaoSpacesUseCase,
        ListarPacotesSublocacaoSpacesUseCase {

    private final SalvarPacoteSublocacaoSpacesPort salvarPacoteSublocacaoSpacesPort;
    private final CarregarPacoteSublocacaoSpacesPorIdPort carregarPacoteSublocacaoSpacesPorIdPort;
    private final ListarPacotesSublocacaoSpacesPort listarPacotesSublocacaoSpacesPort;
    private final CarregarRecursoSpacesPorIdPort carregarRecursoSpacesPorIdPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public PacoteSublocacaoSpacesService(
            SalvarPacoteSublocacaoSpacesPort salvarPacoteSublocacaoSpacesPort,
            CarregarPacoteSublocacaoSpacesPorIdPort carregarPacoteSublocacaoSpacesPorIdPort,
            ListarPacotesSublocacaoSpacesPort listarPacotesSublocacaoSpacesPort,
            CarregarRecursoSpacesPorIdPort carregarRecursoSpacesPorIdPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarPacoteSublocacaoSpacesPort = salvarPacoteSublocacaoSpacesPort;
        this.carregarPacoteSublocacaoSpacesPorIdPort = carregarPacoteSublocacaoSpacesPorIdPort;
        this.listarPacotesSublocacaoSpacesPort = listarPacotesSublocacaoSpacesPort;
        this.carregarRecursoSpacesPorIdPort = carregarRecursoSpacesPorIdPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public PacoteSublocacaoSpacesResult cadastrarPacote(CadastrarPacoteSublocacaoSpacesCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarRecurso(command.recursoId(), empresaId);
        PacoteSublocacaoSpaces pacote = PacoteSublocacaoSpaces.cadastrar(
                empresaId,
                command.recursoId(),
                command.nome(),
                command.tipo(),
                command.descricao(),
                command.duracaoHoras(),
                command.valorFixo(),
                command.percentualReceita(),
                Instant.now(clock)
        );
        salvarPacoteSublocacaoSpacesPort.salvarPacote(pacote);
        return PacoteSublocacaoSpacesResult.de(pacote);
    }

    @Override
    public Optional<PacoteSublocacaoSpacesResult> detalharPacote(UUID pacoteId) {
        validarPermissao();
        return carregarPacoteSublocacaoSpacesPorIdPort.carregarPacotePorId(pacoteId)
                .filter(pacote -> {
                    tenantAccessService.validarAcessoEmpresa(pacote.empresaId());
                    return true;
                })
                .map(PacoteSublocacaoSpacesResult::de);
    }

    @Override
    public ResultadoPaginado<PacoteSublocacaoSpacesResult> listarPacotes(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            UUID recursoId,
            TipoPacoteSublocacaoSpaces tipo,
            Boolean ativo
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        if (recursoId != null) {
            validarRecurso(recursoId, empresaResolvida);
        }
        var pacotes = listarPacotesSublocacaoSpacesPort.listarPacotes(empresaResolvida, paginacao, busca, recursoId, tipo, ativo);
        return new ResultadoPaginado<>(
                pacotes.itens().stream().map(PacoteSublocacaoSpacesResult::de).toList(),
                pacotes.totalItens(),
                pacotes.pagina(),
                pacotes.tamanho()
        );
    }

    private void validarRecurso(UUID recursoId, UUID empresaId) {
        if (recursoId == null) {
            return;
        }
        var recurso = carregarRecursoSpacesPorIdPort.carregarRecursoPorId(recursoId)
                .orElseThrow(() -> new BusinessException("SPACES_RECURSO_NAO_ENCONTRADO", "Recurso Spaces nao encontrado."));
        tenantAccessService.validarAcessoEmpresa(recurso.empresaId());
        if (!recurso.empresaId().equals(empresaId)) {
            throw new BusinessException("SPACES_RECURSO_EMPRESA_DIVERGENTE", "Recurso Spaces nao pertence a empresa informada.");
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
