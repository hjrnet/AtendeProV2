package br.com.atendepro.modules.documento.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.documento.application.command.CriarCarimboProfissionalCommand;
import br.com.atendepro.modules.documento.application.port.in.CriarCarimboProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.DetalharCarimboProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.ListarCarimbosProfissionaisUseCase;
import br.com.atendepro.modules.documento.application.port.out.CarregarCarimboProfissionalPorIdPort;
import br.com.atendepro.modules.documento.application.port.out.ListarCarimbosProfissionaisPort;
import br.com.atendepro.modules.documento.application.port.out.SalvarCarimboProfissionalPort;
import br.com.atendepro.modules.documento.application.result.CarimboProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class CarimboProfissionalService implements
        CriarCarimboProfissionalUseCase,
        DetalharCarimboProfissionalUseCase,
        ListarCarimbosProfissionaisUseCase {

    private final SalvarCarimboProfissionalPort salvarCarimboProfissionalPort;
    private final CarregarCarimboProfissionalPorIdPort carregarCarimboProfissionalPorIdPort;
    private final ListarCarimbosProfissionaisPort listarCarimbosProfissionaisPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public CarimboProfissionalService(
            SalvarCarimboProfissionalPort salvarCarimboProfissionalPort,
            CarregarCarimboProfissionalPorIdPort carregarCarimboProfissionalPorIdPort,
            ListarCarimbosProfissionaisPort listarCarimbosProfissionaisPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarCarimboProfissionalPort = salvarCarimboProfissionalPort;
        this.carregarCarimboProfissionalPorIdPort = carregarCarimboProfissionalPorIdPort;
        this.listarCarimbosProfissionaisPort = listarCarimbosProfissionaisPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public CarimboProfissionalResult criarCarimbo(CriarCarimboProfissionalCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        CarimboProfissional carimbo = CarimboProfissional.criar(
                empresaId,
                command.profissionalId(),
                command.profissionalNome(),
                command.conselho(),
                command.uf(),
                command.numeroRegistro(),
                command.assinaturaTexto(),
                command.clinicaNome(),
                Instant.now(clock)
        );
        salvarCarimboProfissionalPort.salvarCarimbo(carimbo);
        return CarimboProfissionalResult.de(carimbo);
    }

    @Override
    public Optional<CarimboProfissionalResult> detalharCarimbo(UUID carimboId) {
        validarPermissao();
        return carregarCarimboProfissionalPorIdPort.carregarCarimboPorId(carimboId)
                .filter(carimbo -> {
                    tenantAccessService.validarAcessoEmpresa(carimbo.empresaId());
                    return true;
                })
                .map(CarimboProfissionalResult::de);
    }

    @Override
    public ResultadoPaginado<CarimboProfissionalResult> listarCarimbos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            ConselhoProfissional conselho,
            String uf,
            UUID profissionalId,
            Boolean ativo
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        ResultadoPaginado<CarimboProfissional> carimbos = listarCarimbosProfissionaisPort.listarCarimbos(
                empresaResolvida,
                paginacao,
                busca,
                conselho,
                normalizarUf(uf),
                profissionalId,
                ativo
        );
        return new ResultadoPaginado<>(
                carimbos.itens().stream().map(CarimboProfissionalResult::de).toList(),
                carimbos.totalItens(),
                carimbos.pagina(),
                carimbos.tamanho()
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
            throw new BusinessException("CARIMBO_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar carimbos.");
        }
        return empresaIdSolicitada;
    }

    private String normalizarUf(String uf) {
        return uf == null || uf.isBlank() ? null : uf.trim().toUpperCase(Locale.ROOT);
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_DOCUMENTOS);
    }
}
