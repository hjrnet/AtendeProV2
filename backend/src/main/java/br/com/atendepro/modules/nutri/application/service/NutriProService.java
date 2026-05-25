package br.com.atendepro.modules.nutri.application.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import br.com.atendepro.modules.nutri.application.command.ListarAvaliacoesAntropometricasNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarPlanosAlimentaresNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarVisaoNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarProntuarioNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarPacientesNutriProCommand;
import br.com.atendepro.modules.nutri.application.port.in.CriarAvaliacaoAntropometricaNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.CriarPlanoAlimentarNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.DetalharAvaliacaoAntropometricaNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.DetalharPlanoAlimentarNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarProntuarioNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarVisaoNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarAvaliacoesAntropometricasNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarPlanosAlimentaresNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarPacientesNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.out.CarregarAvaliacaoAntropometricaNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarPlanoAlimentarNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarProntuarioNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarVisaoNutriProPort;
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
        DetalharPlanoAlimentarNutriProUseCase {

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
                indicador("documentos", "Documentos", metricas.documentosNutri(), "Documentos nutricionais vinculados a pacientes.", "PREPARADO"),
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
                atalho("gasto-energetico", "Adicionar gasto energético", "Preparar TMB, GEB e GET do paciente na próxima etapa.", "PLANEJADO_R10", "nutri-pro/gasto-energetico"),
                atalho("exames-laboratoriais", "Adicionar exames laboratoriais", "Criar solicitação e histórico de exames em documento profissional.", "PLANEJADO_R10", "nutri-pro/exames"),
                atalho("plano-alimentar", "Adicionar plano alimentar", "Criar plano com refeições, alimentos, suplementos e macros iniciais.", "DISPONIVEL", "nutri-pro/plano-alimentar")
        );
    }

    private List<AtalhoNutriProResult> proximasEvolucoes() {
        return List.of(
                atalho("prontuario", "Prontuário nutricional", "Perfil nutricional com resumo, histórico e menu rápido funcional.", "PROXIMA_TASK", "nutri-pro/prontuario"),
                atalho("avaliacao", "Avaliação antropométrica", "Peso, altura, IMC, objetivos e evolução corporal.", "PLANEJADO_R10", "nutri-pro/avaliacao"),
                atalho("documentos", "Documentos com CRN", "Solicitações, prescrições e carimbo profissional.", "PLANEJADO_R10", "nutri-pro/documentos")
        );
    }

    private AtalhoNutriProResult atalho(String codigo, String titulo, String descricao, String status, String destino) {
        return new AtalhoNutriProResult(codigo, titulo, descricao, status, destino);
    }

    private List<AcaoProntuarioNutriProResult> acoesProntuario() {
        return List.of(
                acaoProntuario("gasto-energetico", "Adicionar gasto energético", "Registrar avaliação e estimar TMB, GEB, GET e meta energética.", StatusAcaoNutriPro.DISPONIVEL, true),
                acaoProntuario("exames-laboratoriais", "Adicionar exames laboratoriais", "Preparar solicitação de exames e histórico do paciente.", StatusAcaoNutriPro.PREPARADO, true),
                acaoProntuario("plano-alimentar", "Adicionar plano alimentar", "Criar plano com refeições, alimentos, suplementos e resumo de macros.", StatusAcaoNutriPro.DISPONIVEL, true),
                acaoProntuario("avaliacao-antropometrica", "Adicionar avaliação antropométrica", "Registrar peso, altura, IMC, objetivo e histórico do paciente.", StatusAcaoNutriPro.DISPONIVEL, false),
                acaoProntuario("anamnese", "Adicionar anamnese", "Organizar queixas, rotina alimentar, preferências e observações.", StatusAcaoNutriPro.PREPARADO, false),
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

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_VERTICAIS);
        permissaoAcessoService.validarPermissao(PermissaoAcesso.VISUALIZAR_EMPRESA);
    }
}
