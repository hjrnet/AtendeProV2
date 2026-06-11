package br.com.atendepro.modules.nutri.application.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.nutri.application.command.CriarAvaliacaoAntropometricaNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.CriarItemPlanoAlimentarNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.CriarPlanoAlimentarNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.CriarRefeicaoPlanoAlimentarNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.DetalharAvaliacaoAntropometricaNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.DetalharPlanoAlimentarNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ConsultarPacienteCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ConsultarRelatorioGerencialCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarExameAvancadoCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarLembreteCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarMaterialEducativoCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarMetaCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarRegistroDiarioCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarSubstituicaoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.RevisarRegistroDiarioCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ArquivarPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.DuplicarPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.EnviarMensagemCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.MarcarMensagensLidasCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.PublicarPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ReorganizarRefeicoesPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.SalvarModeloPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.SubstituirPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.VersionarPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ListarAvaliacoesAntropometricasNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarPlanosAlimentaresNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarVisaoNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarProntuarioNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarPacientesNutriProCommand;
import br.com.atendepro.modules.nutri.application.port.in.CriarAvaliacaoAntropometricaNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.CriarPlanoAlimentarNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.DetalharAvaliacaoAntropometricaNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.DetalharPlanoAlimentarNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.GerenciarExperienciaPacienteNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarProntuarioNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarVisaoNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarAvaliacoesAntropometricasNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarPlanosAlimentaresNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarPacientesNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.out.CarregarAvaliacaoAntropometricaNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarPlanoAlimentarNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarProntuarioNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarVisaoNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ExperienciaPacienteNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ListarAvaliacoesAntropometricasNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ListarPlanosAlimentaresNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ListarPacientesNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.SalvarAvaliacaoAntropometricaNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.SalvarPlanoAlimentarNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.VerificarPacienteNutriProPort;
import br.com.atendepro.modules.nutri.application.result.AcaoProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.AtalhoNutriProResult;
import br.com.atendepro.modules.nutri.application.result.AvaliacaoAntropometricaNutriProResult;
import br.com.atendepro.modules.nutri.application.result.DadosProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.EvolucaoPacienteResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ExameAvancadoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.LembreteAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ListaComprasResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MaterialEducativoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MensagemAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MetaAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RelatorioGerencialNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RegistroDiarioResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.SubstituicaoAlimentarResult;
import br.com.atendepro.modules.nutri.application.result.IndicadorNutriProResult;
import br.com.atendepro.modules.nutri.application.result.MetricasNutriProResult;
import br.com.atendepro.modules.nutri.application.result.PacienteNutriResumoResult;
import br.com.atendepro.modules.nutri.application.result.PlanoAlimentarNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.VisaoNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.AvaliacaoAntropometricaNutriPro;
import br.com.atendepro.modules.nutri.domain.model.ItemPlanoAlimentarNutriPro;
import br.com.atendepro.modules.nutri.domain.model.PlanoAlimentarNutriPro;
import br.com.atendepro.modules.nutri.domain.model.RefeicaoPlanoAlimentarNutriPro;
import br.com.atendepro.modules.nutri.domain.model.StatusAcaoNutriPro;
import br.com.atendepro.modules.nutri.domain.model.StatusOperacionalNutriPro;
import br.com.atendepro.modules.nutri.domain.model.StatusPlanoAlimentarNutriPro;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class NutriProService implements
        ConsultarVisaoNutriProUseCase,
        ListarPacientesNutriProUseCase,
        ConsultarProntuarioNutriProUseCase,
        CriarAvaliacaoAntropometricaNutriProUseCase,
        ListarAvaliacoesAntropometricasNutriProUseCase,
        DetalharAvaliacaoAntropometricaNutriProUseCase,
        CriarPlanoAlimentarNutriProUseCase,
        ListarPlanosAlimentaresNutriProUseCase,
        DetalharPlanoAlimentarNutriProUseCase,
        GerenciarExperienciaPacienteNutriProUseCase {

    private final CarregarVisaoNutriProPort carregarVisaoNutriProPort;
    private final ListarPacientesNutriProPort listarPacientesNutriProPort;
    private final CarregarProntuarioNutriProPort carregarProntuarioNutriProPort;
    private final VerificarPacienteNutriProPort verificarPacienteNutriProPort;
    private final SalvarAvaliacaoAntropometricaNutriProPort salvarAvaliacaoAntropometricaNutriProPort;
    private final ListarAvaliacoesAntropometricasNutriProPort listarAvaliacoesAntropometricasNutriProPort;
    private final CarregarAvaliacaoAntropometricaNutriProPort carregarAvaliacaoAntropometricaNutriProPort;
    private final SalvarPlanoAlimentarNutriProPort salvarPlanoAlimentarNutriProPort;
    private final ListarPlanosAlimentaresNutriProPort listarPlanosAlimentaresNutriProPort;
    private final CarregarPlanoAlimentarNutriProPort carregarPlanoAlimentarNutriProPort;
    private final ExperienciaPacienteNutriProPort experienciaPacienteNutriProPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public NutriProService(
            CarregarVisaoNutriProPort carregarVisaoNutriProPort,
            ListarPacientesNutriProPort listarPacientesNutriProPort,
            CarregarProntuarioNutriProPort carregarProntuarioNutriProPort,
            VerificarPacienteNutriProPort verificarPacienteNutriProPort,
            SalvarAvaliacaoAntropometricaNutriProPort salvarAvaliacaoAntropometricaNutriProPort,
            ListarAvaliacoesAntropometricasNutriProPort listarAvaliacoesAntropometricasNutriProPort,
            CarregarAvaliacaoAntropometricaNutriProPort carregarAvaliacaoAntropometricaNutriProPort,
            SalvarPlanoAlimentarNutriProPort salvarPlanoAlimentarNutriProPort,
            ListarPlanosAlimentaresNutriProPort listarPlanosAlimentaresNutriProPort,
            CarregarPlanoAlimentarNutriProPort carregarPlanoAlimentarNutriProPort,
            ExperienciaPacienteNutriProPort experienciaPacienteNutriProPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarVisaoNutriProPort = carregarVisaoNutriProPort;
        this.listarPacientesNutriProPort = listarPacientesNutriProPort;
        this.carregarProntuarioNutriProPort = carregarProntuarioNutriProPort;
        this.verificarPacienteNutriProPort = verificarPacienteNutriProPort;
        this.salvarAvaliacaoAntropometricaNutriProPort = salvarAvaliacaoAntropometricaNutriProPort;
        this.listarAvaliacoesAntropometricasNutriProPort = listarAvaliacoesAntropometricasNutriProPort;
        this.carregarAvaliacaoAntropometricaNutriProPort = carregarAvaliacaoAntropometricaNutriProPort;
        this.salvarPlanoAlimentarNutriProPort = salvarPlanoAlimentarNutriProPort;
        this.listarPlanosAlimentaresNutriProPort = listarPlanosAlimentaresNutriProPort;
        this.carregarPlanoAlimentarNutriProPort = carregarPlanoAlimentarNutriProPort;
        this.experienciaPacienteNutriProPort = experienciaPacienteNutriProPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public VisaoNutriProResult consultarVisaoNutriPro(ConsultarVisaoNutriProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        MetricasNutriProResult metricas = carregarVisaoNutriProPort.carregarVisaoNutriPro(
                empresaId,
                LocalDate.now(clock)
        );
        StatusOperacionalNutriPro status = StatusOperacionalNutriPro.definir(
                metricas.pacientesAtivos(),
                metricas.servicosNutriAtivos()
        );
        return new VisaoNutriProResult(
                empresaId,
                metricas.empresaNome(),
                status,
                indicadores(metricas),
                atalhosPrioritarios(),
                proximasEvolucoes(),
                metricas.pacientesRecentes(),
                Instant.now(clock)
        );
    }

    @Override
    public List<PacienteNutriResumoResult> listarPacientesNutriPro(ListarPacientesNutriProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        return listarPacientesNutriProPort.listarPacientesNutriPro(empresaId, command.busca());
    }

    @Override
    public Optional<ProntuarioNutriProResult> consultarProntuarioNutriPro(ConsultarProntuarioNutriProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        return carregarProntuarioNutriProPort.carregarProntuarioNutriPro(empresaId, command.pacienteId(), LocalDate.now(clock))
                .map(dados -> montarProntuario(empresaId, dados));
    }

    @Override
    public AvaliacaoAntropometricaNutriProResult criarAvaliacaoAntropometrica(CriarAvaliacaoAntropometricaNutriProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());

        AvaliacaoAntropometricaNutriPro avaliacao = AvaliacaoAntropometricaNutriPro.registrar(
                empresaId,
                command.pacienteId(),
                command.pesoKg(),
                command.alturaCm(),
                command.idade(),
                command.sexo(),
                command.objetivo(),
                command.fatorAtividade(),
                command.observacoes(),
                Instant.now(clock)
        );
        salvarAvaliacaoAntropometricaNutriProPort.salvarAvaliacaoAntropometrica(avaliacao);
        return AvaliacaoAntropometricaNutriProResult.de(avaliacao);
    }

    @Override
    public List<AvaliacaoAntropometricaNutriProResult> listarAvaliacoesAntropometricas(ListarAvaliacoesAntropometricasNutriProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return listarAvaliacoesAntropometricasNutriProPort.listarAvaliacoesAntropometricas(empresaId, command.pacienteId())
                .stream()
                .map(AvaliacaoAntropometricaNutriProResult::de)
                .toList();
    }

    @Override
    public Optional<AvaliacaoAntropometricaNutriProResult> detalharAvaliacaoAntropometrica(DetalharAvaliacaoAntropometricaNutriProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return carregarAvaliacaoAntropometricaNutriProPort
                .carregarAvaliacaoAntropometrica(empresaId, command.pacienteId(), command.avaliacaoId())
                .map(AvaliacaoAntropometricaNutriProResult::de);
    }

    @Override
    @Transactional
    public PlanoAlimentarNutriProResult criarPlanoAlimentar(CriarPlanoAlimentarNutriProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        PlanoAlimentarNutriPro plano = montarPlanoAlimentar(empresaId, command);
        salvarPlanoAlimentarNutriProPort.salvarPlanoAlimentar(plano);
        return PlanoAlimentarNutriProResult.de(plano);
    }

    @Override
    public List<PlanoAlimentarNutriProResult> listarPlanosAlimentares(ListarPlanosAlimentaresNutriProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return listarPlanosAlimentaresNutriProPort.listarPlanosAlimentares(empresaId, command.pacienteId())
                .stream()
                .map(PlanoAlimentarNutriProResult::de)
                .toList();
    }

    @Override
    public Optional<PlanoAlimentarNutriProResult> detalharPlanoAlimentar(DetalharPlanoAlimentarNutriProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return carregarPlanoAlimentarNutriProPort.carregarPlanoAlimentar(empresaId, command.pacienteId(), command.planoId())
                .map(PlanoAlimentarNutriProResult::de);
    }

    @Override
    @Transactional
    public Optional<PlanoAlimentarNutriProResult> publicarPlanoAlimentar(PublicarPlanoAlimentarCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort
                .publicarPlanoAlimentar(empresaId, command.pacienteId(), command.planoId())
                .map(PlanoAlimentarNutriProResult::de);
    }

    @Override
    @Transactional
    public Optional<PlanoAlimentarNutriProResult> substituirPlanoAlimentar(SubstituirPlanoAlimentarCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort
                .substituirPlanoAlimentar(empresaId, command.pacienteId(), command.planoId())
                .map(PlanoAlimentarNutriProResult::de);
    }

    @Override
    @Transactional
    public Optional<PlanoAlimentarNutriProResult> duplicarPlanoAlimentar(DuplicarPlanoAlimentarCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return carregarPlanoAlimentarNutriProPort
                .carregarPlanoAlimentar(empresaId, command.pacienteId(), command.planoId())
                .map(plano -> clonarPlanoAlimentar(
                        plano,
                        StatusPlanoAlimentarNutriPro.RASCUNHO
                ))
                .map(plano -> {
                    salvarPlanoAlimentarNutriProPort.salvarPlanoAlimentar(plano);
                    return PlanoAlimentarNutriProResult.de(plano);
                });
    }

    @Override
    @Transactional
    public Optional<PlanoAlimentarNutriProResult> versionarPlanoAlimentar(VersionarPlanoAlimentarCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return carregarPlanoAlimentarNutriProPort
                .carregarPlanoAlimentar(empresaId, command.pacienteId(), command.planoId())
                .map(plano -> clonarPlanoAlimentar(
                        plano,
                        StatusPlanoAlimentarNutriPro.RASCUNHO
                ))
                .map(plano -> {
                    salvarPlanoAlimentarNutriProPort.salvarPlanoAlimentar(plano);
                    return PlanoAlimentarNutriProResult.de(plano);
                });
    }

    @Override
    @Transactional
    public Optional<PlanoAlimentarNutriProResult> salvarModeloPlanoAlimentar(SalvarModeloPlanoAlimentarCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return carregarPlanoAlimentarNutriProPort
                .carregarPlanoAlimentar(empresaId, command.pacienteId(), command.planoId())
                .map(plano -> clonarPlanoAlimentar(
                        plano,
                        StatusPlanoAlimentarNutriPro.RASCUNHO
                ))
                .map(plano -> {
                    salvarPlanoAlimentarNutriProPort.salvarPlanoAlimentar(plano);
                    return PlanoAlimentarNutriProResult.de(plano);
                });
    }

    @Override
    @Transactional
    public Optional<PlanoAlimentarNutriProResult> arquivarPlanoAlimentar(ArquivarPlanoAlimentarCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort
                .arquivarPlanoAlimentar(empresaId, command.pacienteId(), command.planoId())
                .map(PlanoAlimentarNutriProResult::de);
    }

    @Override
    @Transactional
    public Optional<PlanoAlimentarNutriProResult> reorganizarRefeicoesPlanoAlimentar(ReorganizarRefeicoesPlanoAlimentarCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());

        if (command.refeicaoIds() == null || command.refeicaoIds().isEmpty()) {
            throw new BusinessException("NUTRI_PRO_REFEICOES_ORDEM_INVALIDA", "Reordenacao de refeicoes requer lista nao vazia.");
        }

        Optional<PlanoAlimentarNutriPro> planoExistente = carregarPlanoAlimentarNutriProPort
                .carregarPlanoAlimentar(empresaId, command.pacienteId(), command.planoId());
        if (planoExistente.isEmpty()) {
            return Optional.empty();
        }

        PlanoAlimentarNutriPro plano = planoExistente.get();
        List<UUID> refeicoesDoPlano = plano.refeicoes().stream().map(RefeicaoPlanoAlimentarNutriPro::id).toList();

        if (command.refeicaoIds().size() != refeicoesDoPlano.size()) {
            throw new BusinessException("NUTRI_PRO_REFEICOES_ORDEM_INVALIDA", "Quantidade de refeicoes enviada nao confere com o plano.");
        }

        Set<UUID> refeicoesEsperadas = new HashSet<>(refeicoesDoPlano);
        Set<UUID> refeicoesUnicas = new HashSet<>();

        for (UUID refeicaoId : command.refeicaoIds()) {
            if (refeicaoId == null || !refeicoesEsperadas.remove(refeicaoId) || !refeicoesUnicas.add(refeicaoId)) {
                throw new BusinessException("NUTRI_PRO_REFEICOES_ORDEM_INVALIDA", "Lista de refeicoes invalida para reorganizacao.");
            }
        }

        if (!refeicoesEsperadas.isEmpty()) {
            throw new BusinessException("NUTRI_PRO_REFEICOES_ORDEM_INVALIDA", "Lista de refeicoes invalida para reorganizacao.");
        }

        experienciaPacienteNutriProPort.reorganizarRefeicoesPlanoAlimentar(empresaId, command.pacienteId(), command.planoId(), command.refeicaoIds());

        return carregarPlanoAlimentarNutriProPort
                .carregarPlanoAlimentar(empresaId, command.pacienteId(), command.planoId())
                .map(PlanoAlimentarNutriProResult::de);
    }

    @Override
    public Optional<PlanoAlimentarNutriProResult> consultarPlanoPublicado(ConsultarPacienteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.carregarPlanoPublicado(empresaId, command.pacienteId())
                .map(PlanoAlimentarNutriProResult::de);
    }

    @Override
    public Optional<ListaComprasResult> consultarListaCompras(ConsultarPacienteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.consultarListaCompras(empresaId, command.pacienteId(), clock);
    }

    @Override
    public List<SubstituicaoAlimentarResult> listarSubstituicoesAlimentares(ConsultarPacienteCommand command, UUID planoId) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        validarPlanoPaciente(empresaId, command.pacienteId(), planoId);
        return experienciaPacienteNutriProPort.listarSubstituicoesAlimentares(empresaId, command.pacienteId(), planoId);
    }

    @Override
    @Transactional
    public SubstituicaoAlimentarResult criarSubstituicaoAlimentar(CriarSubstituicaoAlimentarCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        validarPlanoPaciente(empresaId, command.pacienteId(), command.planoId());
        return experienciaPacienteNutriProPort.criarSubstituicaoAlimentar(
                UUID.randomUUID(),
                empresaId,
                command.pacienteId(),
                command.planoId(),
                command.refeicaoId(),
                validarTexto(command.alimentoOrigem(), "NUTRI_PRO_SUBSTITUICAO_ORIGEM_OBRIGATORIA", "Alimento de origem e obrigatorio."),
                validarTexto(command.alimentoSubstituto(), "NUTRI_PRO_SUBSTITUICAO_DESTINO_OBRIGATORIA", "Alimento substituto e obrigatorio."),
                textoOuNulo(command.grupo()),
                textoOuNulo(command.objetivo()),
                textoOuNulo(command.restricaoAlimentar()),
                valorPositivo(command.quantidadeEquivalente(), "NUTRI_PRO_SUBSTITUICAO_QUANTIDADE_INVALIDA", "Quantidade equivalente deve ser positiva."),
                validarTexto(command.unidadeMedida(), "NUTRI_PRO_SUBSTITUICAO_UNIDADE_OBRIGATORIA", "Unidade de medida e obrigatoria."),
                textoOuNulo(command.observacoes())
        );
    }

    @Override
    public List<MaterialEducativoResult> listarMateriaisEducativos(ConsultarPacienteCommand command, UUID planoId) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        validarPlanoPaciente(empresaId, command.pacienteId(), planoId);
        return experienciaPacienteNutriProPort.listarMateriaisEducativos(empresaId, command.pacienteId(), planoId);
    }

    @Override
    @Transactional
    public MaterialEducativoResult criarMaterialEducativo(CriarMaterialEducativoCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        validarPlanoPaciente(empresaId, command.pacienteId(), command.planoId());
        return experienciaPacienteNutriProPort.criarMaterialEducativo(
                UUID.randomUUID(),
                empresaId,
                command.pacienteId(),
                command.planoId(),
                validarTexto(command.tipo(), "NUTRI_PRO_MATERIAL_TIPO_OBRIGATORIO", "Tipo do material e obrigatorio.").toUpperCase(),
                validarTexto(command.titulo(), "NUTRI_PRO_MATERIAL_TITULO_OBRIGATORIO", "Titulo do material e obrigatorio."),
                textoOuNulo(command.objetivo()),
                validarTexto(command.conteudo(), "NUTRI_PRO_MATERIAL_CONTEUDO_OBRIGATORIO", "Conteudo do material e obrigatorio."),
                textoOuNulo(command.linkAnexo()),
                textoOuNulo(command.observacoes())
        );
    }

    @Override
    public List<ExameAvancadoResult> listarExamesAvancados(ConsultarPacienteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.listarExamesAvancados(empresaId, command.pacienteId());
    }

    @Override
    @Transactional
    public ExameAvancadoResult criarExameAvancado(CriarExameAvancadoCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.criarExameAvancado(
                UUID.randomUUID(),
                empresaId,
                command.pacienteId(),
                validarTexto(command.tipo(), "NUTRI_PRO_EXAME_TIPO_OBRIGATORIO", "Tipo do exame ou medida e obrigatorio.").toUpperCase(),
                validarTexto(command.nome(), "NUTRI_PRO_EXAME_NOME_OBRIGATORIO", "Nome do exame ou medida e obrigatorio."),
                valorNaoNegativo(command.valor(), "NUTRI_PRO_EXAME_VALOR_INVALIDO", "Valor do exame ou medida nao pode ser negativo."),
                validarTexto(command.unidadeMedida(), "NUTRI_PRO_EXAME_UNIDADE_OBRIGATORIA", "Unidade de medida e obrigatoria."),
                command.dataExame() == null ? LocalDate.now(clock) : command.dataExame(),
                command.status() == null || command.status().isBlank() ? "REGISTRADO" : command.status().trim().toUpperCase(),
                textoOuNulo(command.observacoes())
        );
    }

    @Override
    public RelatorioGerencialNutriProResult consultarRelatorioGerencial(ConsultarRelatorioGerencialCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        return experienciaPacienteNutriProPort.consultarRelatorioGerencial(empresaId, clock);
    }

    @Override
    public List<RegistroDiarioResult> listarDiarioAlimentar(ConsultarPacienteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.listarDiarioAlimentar(empresaId, command.pacienteId());
    }

    @Override
    public RegistroDiarioResult criarRegistroDiario(CriarRegistroDiarioCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        String texto = validarTexto(command.texto(), "NUTRI_PRO_DIARIO_TEXTO_OBRIGATORIO", "Texto do diário alimentar é obrigatório.");
        UUID planoId = experienciaPacienteNutriProPort.carregarPlanoPublicado(empresaId, command.pacienteId())
                .map(PlanoAlimentarNutriPro::id)
                .orElse(null);
        return experienciaPacienteNutriProPort.criarRegistroDiario(
                UUID.randomUUID(),
                empresaId,
                command.pacienteId(),
                planoId,
                textoOuNulo(command.refeicaoNome()),
                texto,
                textoOuNulo(command.evidenciaUrl()),
                "PACIENTE",
                clock
        );
    }

    @Override
    public Optional<RegistroDiarioResult> revisarRegistroDiario(RevisarRegistroDiarioCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        String parecer = validarTexto(command.parecerProfissional(), "NUTRI_PRO_DIARIO_PARECER_OBRIGATORIO", "Parecer profissional é obrigatório.");
        return experienciaPacienteNutriProPort.revisarRegistroDiario(empresaId, command.pacienteId(), command.registroId(), parecer);
    }

    @Override
    public List<MetaAcompanhamentoResult> listarMetas(ConsultarPacienteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.listarMetas(empresaId, command.pacienteId());
    }

    @Override
    public MetaAcompanhamentoResult criarMeta(CriarMetaCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.criarMeta(
                UUID.randomUUID(),
                empresaId,
                command.pacienteId(),
                validarTexto(command.tipo(), "NUTRI_PRO_META_TIPO_OBRIGATORIO", "Tipo da meta é obrigatório.").toUpperCase(),
                validarTexto(command.descricao(), "NUTRI_PRO_META_DESCRICAO_OBRIGATORIA", "Descrição da meta é obrigatória."),
                command.valorMeta() == null ? BigDecimal.ZERO : command.valorMeta(),
                textoOuNulo(command.unidade()),
                command.dataAlvo(),
                clock
        );
    }

    @Override
    public List<LembreteAcompanhamentoResult> listarLembretes(ConsultarPacienteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.listarLembretes(empresaId, command.pacienteId());
    }

    @Override
    public LembreteAcompanhamentoResult criarLembrete(CriarLembreteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.criarLembrete(
                UUID.randomUUID(),
                empresaId,
                command.pacienteId(),
                validarTexto(command.titulo(), "NUTRI_PRO_LEMBRETE_TITULO_OBRIGATORIO", "Título do lembrete é obrigatório."),
                textoOuNulo(command.descricao()),
                textoOuNulo(command.horario()),
                validarTexto(command.frequencia(), "NUTRI_PRO_LEMBRETE_FREQUENCIA_OBRIGATORIA", "Frequência do lembrete é obrigatória.").toUpperCase(),
                clock
        );
    }

    @Override
    public List<MensagemAcompanhamentoResult> listarMensagens(ConsultarPacienteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.listarMensagens(empresaId, command.pacienteId());
    }

    @Override
    public MensagemAcompanhamentoResult enviarMensagem(EnviarMensagemCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.enviarMensagem(
                UUID.randomUUID(),
                empresaId,
                command.pacienteId(),
                validarTexto(command.remetenteTipo(), "NUTRI_PRO_MENSAGEM_REMETENTE_TIPO_OBRIGATORIO", "Tipo do remetente é obrigatório.").toUpperCase(),
                validarTexto(command.remetenteNome(), "NUTRI_PRO_MENSAGEM_REMETENTE_OBRIGATORIO", "Remetente é obrigatório."),
                validarTexto(command.texto(), "NUTRI_PRO_MENSAGEM_TEXTO_OBRIGATORIO", "Texto da mensagem é obrigatório."),
                textoOuNulo(command.contexto()),
                clock
        );
    }

    @Override
    public void marcarMensagensLidas(MarcarMensagensLidasCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        experienciaPacienteNutriProPort.marcarMensagensLidas(
                empresaId,
                command.pacienteId(),
                validarTexto(command.leitor(), "NUTRI_PRO_MENSAGEM_LEITOR_OBRIGATORIO", "Leitor é obrigatório.").toUpperCase()
        );
    }

    @Override
    public List<EvolucaoPacienteResult> listarEvolucao(ConsultarPacienteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarPacienteNutriPro(empresaId, command.pacienteId());
        return experienciaPacienteNutriProPort.listarEvolucao(empresaId, command.pacienteId());
    }

    private ProntuarioNutriProResult montarProntuario(UUID empresaId, DadosProntuarioNutriProResult dados) {
        return new ProntuarioNutriProResult(
                empresaId,
                dados.paciente(),
                dados.resumo(),
                acoesProntuario(),
                Instant.now(clock)
        );
    }

    private List<IndicadorNutriProResult> indicadores(MetricasNutriProResult metricas) {
        return List.of(
                indicador("pacientes", "Pacientes Nutri", metricas.pacientesAtivos(), "Pacientes ativos da área de nutrição.", "OPERACIONAL"),
                indicador("agendaHoje", "Agenda hoje", metricas.agendaHoje(), "Atendimentos nutricionais previstos para hoje.", "OPERACIONAL"),
                indicador("agenda7Dias", "Próximos 7 dias", metricas.agendaProximos7Dias(), "Consultas e retornos nutricionais da semana.", "OPERACIONAL"),
                indicador("servicos", "Serviços Nutri", metricas.servicosNutriAtivos(), "Procedimentos e serviços ativos da vertical.", "OPERACIONAL"),
                indicador("avaliacoes", "Avaliações", metricas.avaliacoesAntropometricas(), "Avaliações antropométricas registradas para pacientes Nutri.", "OPERACIONAL"),
                indicador("exames", "Exames solicitados", metricas.examesLaboratoriais(), "Solicitações laboratoriais emitidas pelo Nutri Pro.", "OPERACIONAL"),
                indicador("prescricoes", "Prescrições", metricas.prescricoesNutri(), "Prescrições e orientações nutricionais registradas.", "OPERACIONAL"),
                indicador("documentos", "Documentos", metricas.documentosNutri(), "Documentos nutricionais vinculados a pacientes.", "OPERACIONAL"),
                indicador("precificacao", "Precificação", metricas.simulacoesPrecificacao(), "Simulações de custo real para serviços de nutrição.", "OPERACIONAL"),
                indicador("alertas", "Alertas de preço", metricas.simulacoesEmAlerta(), "Simulações com margem baixa ou prejuízo.", metricas.simulacoesEmAlerta() > 0 ? "ALERTA" : "SAUDAVEL"),
                indicador("planos", "Planos alimentares", metricas.planosAlimentaresAtivos(), "Planos alimentares ativos por paciente.", "OPERACIONAL")
        );
    }

    private IndicadorNutriProResult indicador(String codigo, String titulo, long valor, String descricao, String status) {
        return new IndicadorNutriProResult(codigo, titulo, valor, descricao, status);
    }

    private List<AtalhoNutriProResult> atalhosPrioritarios() {
        return List.of(
                atalho("gasto-energetico", "Adicionar gasto energético", "Registrar avaliação e estimar TMB, GEB e GET do paciente.", "DISPONIVEL", "nutri-pro/gasto-energetico"),
                atalho("exames-laboratoriais", "Adicionar exames laboratoriais", "Criar solicitação e histórico de exames em documento profissional.", "DISPONIVEL", "nutri-pro/exames"),
                atalho("plano-alimentar", "Adicionar plano alimentar", "Criar plano com refeições, alimentos, suplementos e macros iniciais.", "DISPONIVEL", "nutri-pro/plano-alimentar")
        );
    }

    private List<AtalhoNutriProResult> proximasEvolucoes() {
        return List.of(
                atalho("prontuario", "Prontuário nutricional", "Perfil nutricional com resumo, histórico e menu rápido funcional.", "DISPONIVEL", "nutri-pro/prontuario"),
                atalho("avaliacao", "Avaliação antropométrica", "Peso, altura, IMC, objetivos e evolução corporal.", "DISPONIVEL", "nutri-pro/avaliacao"),
                atalho("documentos", "Documentos com CRN", "Solicitações, prescrições e carimbo profissional.", "DISPONIVEL", "nutri-pro/documentos")
        );
    }

    private AtalhoNutriProResult atalho(String codigo, String titulo, String descricao, String status, String destino) {
        return new AtalhoNutriProResult(codigo, titulo, descricao, status, destino);
    }

    private List<AcaoProntuarioNutriProResult> acoesProntuario() {
        return List.of(
                acaoProntuario("gasto-energetico", "Adicionar gasto energético", "Registrar avaliação e estimar TMB, GEB, GET e meta energética.", StatusAcaoNutriPro.DISPONIVEL, true),
                acaoProntuario("exames-laboratoriais", "Adicionar exames laboratoriais", "Preparar solicitação de exames e histórico do paciente.", StatusAcaoNutriPro.DISPONIVEL, true),
                acaoProntuario("plano-alimentar", "Adicionar plano alimentar", "Criar plano com refeições, alimentos, suplementos e resumo de macros.", StatusAcaoNutriPro.DISPONIVEL, true),
                acaoProntuario("avaliacao-antropometrica", "Adicionar avaliação antropométrica", "Registrar peso, altura, IMC, objetivo e histórico do paciente.", StatusAcaoNutriPro.DISPONIVEL, false),
                acaoProntuario("anamnese", "Adicionar anamnese", "Organizar queixas, rotina alimentar, preferências e observações.", StatusAcaoNutriPro.PREPARADO, false),
                acaoProntuario("prescricoes", "Adicionar prescrição", "Registrar suplementação, formulações e orientações nutricionais.", StatusAcaoNutriPro.DISPONIVEL, false),
                acaoProntuario("metas", "Adicionar metas", "Definir objetivos de acompanhamento nutricional.", StatusAcaoNutriPro.PREPARADO, false)
        );
    }

    private AcaoProntuarioNutriProResult acaoProntuario(
            String codigo,
            String titulo,
            String descricao,
            StatusAcaoNutriPro status,
            boolean destaque
    ) {
        return new AcaoProntuarioNutriProResult(codigo, titulo, descricao, status, destaque);
    }

    private PlanoAlimentarNutriPro montarPlanoAlimentar(UUID empresaId, CriarPlanoAlimentarNutriProCommand command) {
        if (command.refeicoes() == null || command.refeicoes().isEmpty()) {
            throw new BusinessException("NUTRI_PRO_PLANO_SEM_REFEICOES", "Plano alimentar deve ter ao menos uma refeicao.");
        }
        UUID planoId = UUID.randomUUID();
        List<RefeicaoPlanoAlimentarNutriPro> refeicoes = new ArrayList<>();
        for (CriarRefeicaoPlanoAlimentarNutriProCommand refeicaoCommand : command.refeicoes()) {
            UUID refeicaoId = UUID.randomUUID();
            List<ItemPlanoAlimentarNutriPro> itens = montarItensRefeicao(empresaId, refeicaoId, refeicaoCommand.itens());
            refeicoes.add(new RefeicaoPlanoAlimentarNutriPro(
                    refeicaoId,
                    empresaId,
                    planoId,
                    refeicaoCommand.nome(),
                    refeicaoCommand.horario(),
                    refeicaoCommand.observacoes(),
                    refeicaoCommand.ordenacao(),
                    itens,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            ));
        }
        Instant agora = Instant.now(clock);
        return new PlanoAlimentarNutriPro(
                planoId,
                empresaId,
                command.pacienteId(),
                command.objetivo(),
                command.descricao(),
                command.status() == null ? StatusPlanoAlimentarNutriPro.RASCUNHO : command.status(),
                refeicoes,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                agora,
                agora
        );
    }

    private List<ItemPlanoAlimentarNutriPro> montarItensRefeicao(
            UUID empresaId,
            UUID refeicaoId,
            List<CriarItemPlanoAlimentarNutriProCommand> itensCommand
    ) {
        if (itensCommand == null || itensCommand.isEmpty()) {
            throw new BusinessException("NUTRI_PRO_REFEICAO_SEM_ITENS", "Refeicao do plano alimentar deve ter ao menos um item.");
        }
        return itensCommand.stream()
                .map(item -> ItemPlanoAlimentarNutriPro.criar(
                        empresaId,
                        refeicaoId,
                        item.tipoItem(),
                        item.nome(),
                        item.grupo(),
                        item.unidadeMedida(),
                        item.quantidadeBase(),
                        item.quantidade(),
                        item.energiaKcalBase(),
                        item.proteinasBase(),
                        item.carboidratosBase(),
                        item.lipidiosBase(),
                        item.observacoes(),
                        item.ordenacao()
                ))
                .toList();
    }

    private PlanoAlimentarNutriPro clonarPlanoAlimentar(PlanoAlimentarNutriPro plano, StatusPlanoAlimentarNutriPro status) {
        UUID novoPlanoId = UUID.randomUUID();
        List<RefeicaoPlanoAlimentarNutriPro> refeicoes = plano.refeicoes().stream()
                .map(refeicao -> clonarRefeicao(novoPlanoId, plano.empresaId(), refeicao))
                .toList();
        return new PlanoAlimentarNutriPro(
                novoPlanoId,
                plano.empresaId(),
                plano.pacienteId(),
                plano.objetivo(),
                plano.descricao(),
                status == null ? StatusPlanoAlimentarNutriPro.RASCUNHO : status,
                refeicoes,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.now(clock),
                Instant.now(clock)
        );
    }

    private RefeicaoPlanoAlimentarNutriPro clonarRefeicao(
            UUID novoPlanoId,
            UUID empresaId,
            RefeicaoPlanoAlimentarNutriPro refeicao
    ) {
        List<ItemPlanoAlimentarNutriPro> itens = refeicao.itens().stream()
                .map(item -> clonarItem(novoPlanoId, item, empresaId))
                .toList();
        return new RefeicaoPlanoAlimentarNutriPro(
                UUID.randomUUID(),
                empresaId,
                novoPlanoId,
                refeicao.nome(),
                refeicao.horario(),
                refeicao.observacoes(),
                refeicao.ordenacao(),
                itens,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    private ItemPlanoAlimentarNutriPro clonarItem(
            UUID novoPlanoId,
            ItemPlanoAlimentarNutriPro item,
            UUID empresaId
    ) {
        return new ItemPlanoAlimentarNutriPro(
                UUID.randomUUID(),
                empresaId,
                novoPlanoId,
                item.tipoItem(),
                item.nome(),
                item.grupo(),
                item.unidadeMedida(),
                item.quantidadeBase(),
                item.quantidade(),
                item.energiaKcalBase(),
                item.proteinasBase(),
                item.carboidratosBase(),
                item.lipidiosBase(),
                item.energiaKcal(),
                item.proteinas(),
                item.carboidratos(),
                item.lipidios(),
                item.observacoes(),
                item.ordenacao()
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
            throw new BusinessException("NUTRI_PRO_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar Nutri Pro.");
        }
        return empresaIdSolicitada;
    }

    private void validarPacienteNutriPro(UUID empresaId, UUID pacienteId) {
        if (pacienteId == null || !verificarPacienteNutriProPort.existePacienteNutriPro(empresaId, pacienteId)) {
            throw new BusinessException("NUTRI_PRO_PACIENTE_NAO_ENCONTRADO", "Paciente de nutricao nao encontrado nesta empresa.");
        }
    }

    private void validarPlanoPaciente(UUID empresaId, UUID pacienteId, UUID planoId) {
        if (planoId == null || carregarPlanoAlimentarNutriProPort.carregarPlanoAlimentar(empresaId, pacienteId, planoId).isEmpty()) {
            throw new BusinessException("NUTRI_PRO_PLANO_NAO_ENCONTRADO", "Plano alimentar nao encontrado para o paciente.");
        }
    }

    private String validarTexto(String valor, String codigo, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException(codigo, mensagem);
        }
        return valor.trim();
    }

    private String textoOuNulo(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private BigDecimal valorPositivo(BigDecimal valor, String codigo, String mensagem) {
        if (valor == null || valor.signum() <= 0) {
            throw new BusinessException(codigo, mensagem);
        }
        return valor;
    }

    private BigDecimal valorNaoNegativo(BigDecimal valor, String codigo, String mensagem) {
        if (valor == null || valor.signum() < 0) {
            throw new BusinessException(codigo, mensagem);
        }
        return valor;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_VERTICAIS);
        permissaoAcessoService.validarPermissao(PermissaoAcesso.VISUALIZAR_EMPRESA);
    }
}
