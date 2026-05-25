package br.com.atendepro.modules.assinatura.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.adminsaas.application.port.out.CarregarEmpresaAdminSaasPort;
import br.com.atendepro.modules.assinatura.application.command.AlterarPlanoAssinaturaCommand;
import br.com.atendepro.modules.assinatura.application.command.CriarAssinaturaCommand;
import br.com.atendepro.modules.assinatura.application.port.in.AlterarPlanoAssinaturaUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.BuscarAssinaturaUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.CriarAssinaturaUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.GerenciarStatusAssinaturaUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.ListarAssinaturasUseCase;
import br.com.atendepro.modules.assinatura.application.port.out.AtualizarAssinaturaPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarAssinaturaAtivaPorEmpresaPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarAssinaturaPorIdPort;
import br.com.atendepro.modules.assinatura.application.port.out.ListarAssinaturasPort;
import br.com.atendepro.modules.assinatura.application.port.out.SalvarAssinaturaPort;
import br.com.atendepro.modules.assinatura.application.result.AssinaturaResult;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaSaas;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaStatus;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorIdPort;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class AssinaturaService implements
        CriarAssinaturaUseCase,
        BuscarAssinaturaUseCase,
        ListarAssinaturasUseCase,
        AlterarPlanoAssinaturaUseCase,
        GerenciarStatusAssinaturaUseCase {

    private final PermissaoAcessoService permissaoAcessoService;
    private final CarregarEmpresaAdminSaasPort carregarEmpresaAdminSaasPort;
    private final CarregarPlanoPorIdPort carregarPlanoPorIdPort;
    private final CarregarAssinaturaAtivaPorEmpresaPort carregarAssinaturaAtivaPorEmpresaPort;
    private final CarregarAssinaturaPorIdPort carregarAssinaturaPorIdPort;
    private final SalvarAssinaturaPort salvarAssinaturaPort;
    private final AtualizarAssinaturaPort atualizarAssinaturaPort;
    private final ListarAssinaturasPort listarAssinaturasPort;
    private final Clock clock;

    public AssinaturaService(
            PermissaoAcessoService permissaoAcessoService,
            CarregarEmpresaAdminSaasPort carregarEmpresaAdminSaasPort,
            CarregarPlanoPorIdPort carregarPlanoPorIdPort,
            CarregarAssinaturaAtivaPorEmpresaPort carregarAssinaturaAtivaPorEmpresaPort,
            CarregarAssinaturaPorIdPort carregarAssinaturaPorIdPort,
            SalvarAssinaturaPort salvarAssinaturaPort,
            AtualizarAssinaturaPort atualizarAssinaturaPort,
            ListarAssinaturasPort listarAssinaturasPort,
            Clock clock
    ) {
        this.permissaoAcessoService = permissaoAcessoService;
        this.carregarEmpresaAdminSaasPort = carregarEmpresaAdminSaasPort;
        this.carregarPlanoPorIdPort = carregarPlanoPorIdPort;
        this.carregarAssinaturaAtivaPorEmpresaPort = carregarAssinaturaAtivaPorEmpresaPort;
        this.carregarAssinaturaPorIdPort = carregarAssinaturaPorIdPort;
        this.salvarAssinaturaPort = salvarAssinaturaPort;
        this.atualizarAssinaturaPort = atualizarAssinaturaPort;
        this.listarAssinaturasPort = listarAssinaturasPort;
        this.clock = clock;
    }

    @Override
    public AssinaturaResult criarAssinatura(CriarAssinaturaCommand command) {
        validarAcessoAdminSaas();
        validarEmpresa(command.empresaId());
        validarPlano(command.planoId());
        carregarAssinaturaAtivaPorEmpresaPort.carregarAssinaturaAtivaPorEmpresa(command.empresaId())
                .ifPresent(assinatura -> {
                    throw new BusinessException("ASSINATURA_ATIVA_JA_EXISTE", "Empresa ja possui assinatura ativa.");
                });

        AssinaturaSaas assinatura = AssinaturaSaas.criar(command.empresaId(), command.planoId(), Instant.now(clock));
        salvarAssinaturaPort.salvarAssinatura(assinatura);
        return AssinaturaResult.de(assinatura);
    }

    @Override
    public Optional<AssinaturaResult> buscarAssinaturaPorId(UUID assinaturaId) {
        validarAcessoAdminSaas();
        return carregarAssinaturaPorIdPort.carregarAssinaturaPorId(assinaturaId).map(AssinaturaResult::de);
    }

    @Override
    public ResultadoPaginado<AssinaturaResult> listarAssinaturas(Paginacao paginacao, AssinaturaStatus status) {
        validarAcessoAdminSaas();
        var assinaturas = listarAssinaturasPort.listarAssinaturas(paginacao, status);
        return new ResultadoPaginado<>(
                assinaturas.itens().stream().map(AssinaturaResult::de).toList(),
                assinaturas.totalItens(),
                assinaturas.pagina(),
                assinaturas.tamanho()
        );
    }

    @Override
    public Optional<AssinaturaResult> alterarPlanoAssinatura(AlterarPlanoAssinaturaCommand command) {
        validarAcessoAdminSaas();
        validarPlano(command.planoId());
        return carregarAssinaturaPorIdPort.carregarAssinaturaPorId(command.assinaturaId())
                .map(assinatura -> atualizar(() -> assinatura.alterarPlano(command.planoId(), Instant.now(clock))));
    }

    @Override
    public Optional<AssinaturaResult> cancelarAssinatura(UUID assinaturaId) {
        validarAcessoAdminSaas();
        return carregarAssinaturaPorIdPort.carregarAssinaturaPorId(assinaturaId)
                .map(assinatura -> atualizar(() -> assinatura.cancelar(Instant.now(clock))));
    }

    @Override
    public Optional<AssinaturaResult> bloquearAssinatura(UUID assinaturaId) {
        validarAcessoAdminSaas();
        return carregarAssinaturaPorIdPort.carregarAssinaturaPorId(assinaturaId)
                .map(assinatura -> atualizar(() -> assinatura.bloquear(Instant.now(clock))));
    }

    @Override
    public Optional<AssinaturaResult> desbloquearAssinatura(UUID assinaturaId) {
        validarAcessoAdminSaas();
        return carregarAssinaturaPorIdPort.carregarAssinaturaPorId(assinaturaId)
                .map(assinatura -> atualizar(() -> assinatura.desbloquear(Instant.now(clock))));
    }

    private AssinaturaResult atualizar(java.util.function.Supplier<AssinaturaSaas> operacao) {
        try {
            AssinaturaSaas assinatura = operacao.get();
            atualizarAssinaturaPort.atualizarAssinatura(assinatura);
            return AssinaturaResult.de(assinatura);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("ASSINATURA_OPERACAO_INVALIDA", exception.getMessage());
        }
    }

    private void validarEmpresa(UUID empresaId) {
        if (carregarEmpresaAdminSaasPort.carregarEmpresa(empresaId).isEmpty()) {
            throw new BusinessException("EMPRESA_NAO_ENCONTRADA", "Empresa nao encontrada para assinatura.");
        }
    }

    private void validarPlano(UUID planoId) {
        if (carregarPlanoPorIdPort.carregarPlanoPorId(planoId).isEmpty()) {
            throw new BusinessException("PLANO_NAO_ENCONTRADO", "Plano nao encontrado para assinatura.");
        }
    }

    private void validarAcessoAdminSaas() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
    }
}
