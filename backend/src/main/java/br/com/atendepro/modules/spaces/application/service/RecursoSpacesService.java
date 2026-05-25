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
import br.com.atendepro.modules.spaces.application.command.CadastrarRecursoSpacesCommand;
import br.com.atendepro.modules.spaces.application.command.CalcularCustoHoraSpacesCommand;
import br.com.atendepro.modules.spaces.application.port.in.CalcularCustoHoraSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.CadastrarRecursoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.DetalharRecursoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ListarRecursosSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.out.CarregarRecursoSpacesPorIdPort;
import br.com.atendepro.modules.spaces.application.port.out.ListarRecursosSpacesPort;
import br.com.atendepro.modules.spaces.application.port.out.SalvarRecursoSpacesPort;
import br.com.atendepro.modules.spaces.application.result.CustoHoraSpacesResult;
import br.com.atendepro.modules.spaces.application.result.RecursoSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.CustoHoraSpaces;
import br.com.atendepro.modules.spaces.domain.model.RecursoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class RecursoSpacesService implements
        CalcularCustoHoraSpacesUseCase,
        CadastrarRecursoSpacesUseCase,
        DetalharRecursoSpacesUseCase,
        ListarRecursosSpacesUseCase {

    private final SalvarRecursoSpacesPort salvarRecursoSpacesPort;
    private final CarregarRecursoSpacesPorIdPort carregarRecursoSpacesPorIdPort;
    private final ListarRecursosSpacesPort listarRecursosSpacesPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public RecursoSpacesService(
            SalvarRecursoSpacesPort salvarRecursoSpacesPort,
            CarregarRecursoSpacesPorIdPort carregarRecursoSpacesPorIdPort,
            ListarRecursosSpacesPort listarRecursosSpacesPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarRecursoSpacesPort = salvarRecursoSpacesPort;
        this.carregarRecursoSpacesPorIdPort = carregarRecursoSpacesPorIdPort;
        this.listarRecursosSpacesPort = listarRecursosSpacesPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public CustoHoraSpacesResult calcularCustoHora(CalcularCustoHoraSpacesCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        RecursoSpaces recurso = null;
        if (command.recursoId() != null) {
            recurso = carregarRecursoSpacesPorIdPort.carregarRecursoPorId(command.recursoId())
                    .orElseThrow(() -> new BusinessException("SPACES_RECURSO_NAO_ENCONTRADO", "Recurso Spaces nao encontrado."));
            tenantAccessService.validarAcessoEmpresa(recurso.empresaId());
            if (!recurso.empresaId().equals(empresaId)) {
                throw new BusinessException("SPACES_RECURSO_EMPRESA_DIVERGENTE", "Recurso Spaces nao pertence a empresa informada.");
            }
        }
        CustoHoraSpaces custoHora = CustoHoraSpaces.calcular(
                command.recursoId(),
                recurso == null ? null : recurso.nome(),
                recurso == null ? null : recurso.tipo(),
                command.custoFixoMensal(),
                command.diasDisponiveisMes(),
                command.horasDisponiveisDia()
        );
        return CustoHoraSpacesResult.de(empresaId, custoHora);
    }

    @Override
    public RecursoSpacesResult cadastrarRecurso(CadastrarRecursoSpacesCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        RecursoSpaces recurso = RecursoSpaces.cadastrar(
                empresaId,
                command.nome(),
                command.tipo(),
                command.descricao(),
                command.capacidadePessoas(),
                command.localizacao(),
                Instant.now(clock)
        );
        salvarRecursoSpacesPort.salvarRecurso(recurso);
        return RecursoSpacesResult.de(recurso);
    }

    @Override
    public Optional<RecursoSpacesResult> detalharRecurso(UUID recursoId) {
        validarPermissao();
        return carregarRecursoSpacesPorIdPort.carregarRecursoPorId(recursoId)
                .filter(recurso -> {
                    tenantAccessService.validarAcessoEmpresa(recurso.empresaId());
                    return true;
                })
                .map(RecursoSpacesResult::de);
    }

    @Override
    public ResultadoPaginado<RecursoSpacesResult> listarRecursos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoRecursoSpaces tipo,
            Boolean ativo
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        var recursos = listarRecursosSpacesPort.listarRecursos(empresaResolvida, paginacao, busca, tipo, ativo);
        return new ResultadoPaginado<>(
                recursos.itens().stream().map(RecursoSpacesResult::de).toList(),
                recursos.totalItens(),
                recursos.pagina(),
                recursos.tamanho()
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
            throw new BusinessException("SPACES_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar Spaces.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_SPACES);
    }
}
