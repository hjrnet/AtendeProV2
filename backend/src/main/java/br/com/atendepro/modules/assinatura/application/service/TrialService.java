package br.com.atendepro.modules.assinatura.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.adminsaas.application.port.out.CarregarEmpresaAdminSaasPort;
import br.com.atendepro.modules.assinatura.application.command.IniciarTrialCommand;
import br.com.atendepro.modules.assinatura.application.port.in.BuscarTrialUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.ConverterTrialUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.IniciarTrialUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.ListarTrialsUseCase;
import br.com.atendepro.modules.assinatura.application.port.out.AtualizarTrialPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarTrialAtivoPorEmpresaPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarTrialPorIdPort;
import br.com.atendepro.modules.assinatura.application.port.out.ListarTrialsPort;
import br.com.atendepro.modules.assinatura.application.port.out.SalvarTrialPort;
import br.com.atendepro.modules.assinatura.application.result.TrialResult;
import br.com.atendepro.modules.assinatura.domain.model.TrialAssinatura;
import br.com.atendepro.modules.assinatura.domain.model.TrialStatus;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorIdPort;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class TrialService implements IniciarTrialUseCase, BuscarTrialUseCase, ConverterTrialUseCase, ListarTrialsUseCase {

    private final PermissaoAcessoService permissaoAcessoService;
    private final CarregarEmpresaAdminSaasPort carregarEmpresaAdminSaasPort;
    private final CarregarPlanoPorIdPort carregarPlanoPorIdPort;
    private final CarregarTrialAtivoPorEmpresaPort carregarTrialAtivoPorEmpresaPort;
    private final CarregarTrialPorIdPort carregarTrialPorIdPort;
    private final SalvarTrialPort salvarTrialPort;
    private final AtualizarTrialPort atualizarTrialPort;
    private final ListarTrialsPort listarTrialsPort;
    private final Clock clock;

    public TrialService(
            PermissaoAcessoService permissaoAcessoService,
            CarregarEmpresaAdminSaasPort carregarEmpresaAdminSaasPort,
            CarregarPlanoPorIdPort carregarPlanoPorIdPort,
            CarregarTrialAtivoPorEmpresaPort carregarTrialAtivoPorEmpresaPort,
            CarregarTrialPorIdPort carregarTrialPorIdPort,
            SalvarTrialPort salvarTrialPort,
            AtualizarTrialPort atualizarTrialPort,
            ListarTrialsPort listarTrialsPort,
            Clock clock
    ) {
        this.permissaoAcessoService = permissaoAcessoService;
        this.carregarEmpresaAdminSaasPort = carregarEmpresaAdminSaasPort;
        this.carregarPlanoPorIdPort = carregarPlanoPorIdPort;
        this.carregarTrialAtivoPorEmpresaPort = carregarTrialAtivoPorEmpresaPort;
        this.carregarTrialPorIdPort = carregarTrialPorIdPort;
        this.salvarTrialPort = salvarTrialPort;
        this.atualizarTrialPort = atualizarTrialPort;
        this.listarTrialsPort = listarTrialsPort;
        this.clock = clock;
    }

    @Override
    public TrialResult iniciarTrial(IniciarTrialCommand command) {
        validarAcessoAdminSaas();
        Instant agora = Instant.now(clock);
        validarEmpresa(command.empresaId());
        validarPlano(command.planoId());
        carregarTrialAtivoPorEmpresaPort.carregarTrialAtivoPorEmpresa(command.empresaId())
                .filter(trial -> trial.statusEm(agora) == TrialStatus.ATIVO)
                .ifPresent(trial -> {
                    throw new BusinessException("TRIAL_ATIVO_JA_EXISTE", "Empresa ja possui trial ativo.");
                });

        TrialAssinatura trial = TrialAssinatura.iniciar(command.empresaId(), command.planoId(), agora);
        salvarTrialPort.salvarTrial(trial);
        return TrialResult.de(trial, agora);
    }

    @Override
    public Optional<TrialResult> buscarTrialPorId(UUID trialId) {
        validarAcessoAdminSaas();
        Instant agora = Instant.now(clock);
        return carregarTrialPorIdPort.carregarTrialPorId(trialId)
                .map(trial -> TrialResult.de(trial, agora));
    }

    @Override
    public Optional<TrialResult> converterTrial(UUID trialId) {
        validarAcessoAdminSaas();
        Instant agora = Instant.now(clock);
        return carregarTrialPorIdPort.carregarTrialPorId(trialId)
                .map(trial -> converter(trial, agora));
    }

    @Override
    public ResultadoPaginado<TrialResult> listarTrials(Paginacao paginacao, TrialStatus status) {
        validarAcessoAdminSaas();
        Instant agora = Instant.now(clock);
        var trials = listarTrialsPort.listarTrials(paginacao, status);
        return new ResultadoPaginado<>(
                trials.itens().stream().map(trial -> TrialResult.de(trial, agora)).toList(),
                trials.totalItens(),
                trials.pagina(),
                trials.tamanho()
        );
    }

    private TrialResult converter(TrialAssinatura trial, Instant agora) {
        try {
            TrialAssinatura convertido = trial.converter(agora);
            atualizarTrialPort.atualizarTrial(convertido);
            return TrialResult.de(convertido, agora);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("TRIAL_NAO_CONVERTIVEL", exception.getMessage());
        }
    }

    private void validarEmpresa(UUID empresaId) {
        if (carregarEmpresaAdminSaasPort.carregarEmpresa(empresaId).isEmpty()) {
            throw new BusinessException("EMPRESA_NAO_ENCONTRADA", "Empresa nao encontrada para iniciar trial.");
        }
    }

    private void validarPlano(UUID planoId) {
        if (carregarPlanoPorIdPort.carregarPlanoPorId(planoId).isEmpty()) {
            throw new BusinessException("PLANO_NAO_ENCONTRADO", "Plano nao encontrado para iniciar trial.");
        }
    }

    private void validarAcessoAdminSaas() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
    }
}
