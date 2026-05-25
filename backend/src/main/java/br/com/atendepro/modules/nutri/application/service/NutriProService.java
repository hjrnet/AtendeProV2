package br.com.atendepro.modules.nutri.application.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.nutri.application.command.ConsultarVisaoNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarProntuarioNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarPacientesNutriProCommand;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarProntuarioNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarVisaoNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarPacientesNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.out.CarregarProntuarioNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.CarregarVisaoNutriProPort;
import br.com.atendepro.modules.nutri.application.port.out.ListarPacientesNutriProPort;
import br.com.atendepro.modules.nutri.application.result.AcaoProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.AtalhoNutriProResult;
import br.com.atendepro.modules.nutri.application.result.DadosProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.IndicadorNutriProResult;
import br.com.atendepro.modules.nutri.application.result.MetricasNutriProResult;
import br.com.atendepro.modules.nutri.application.result.PacienteNutriResumoResult;
import br.com.atendepro.modules.nutri.application.result.ProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.VisaoNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.StatusAcaoNutriPro;
import br.com.atendepro.modules.nutri.domain.model.StatusOperacionalNutriPro;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class NutriProService implements
        ConsultarVisaoNutriProUseCase,
        ListarPacientesNutriProUseCase,
        ConsultarProntuarioNutriProUseCase {

    private final CarregarVisaoNutriProPort carregarVisaoNutriProPort;
    private final ListarPacientesNutriProPort listarPacientesNutriProPort;
    private final CarregarProntuarioNutriProPort carregarProntuarioNutriProPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public NutriProService(
            CarregarVisaoNutriProPort carregarVisaoNutriProPort,
            ListarPacientesNutriProPort listarPacientesNutriProPort,
            CarregarProntuarioNutriProPort carregarProntuarioNutriProPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarVisaoNutriProPort = carregarVisaoNutriProPort;
        this.listarPacientesNutriProPort = listarPacientesNutriProPort;
        this.carregarProntuarioNutriProPort = carregarProntuarioNutriProPort;
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
                indicador("planos", "Planos alimentares", metricas.planosAlimentaresAtivos(), "Reservado para a task de plano alimentar.", "PLANEJADO")
        );
    }

    private IndicadorNutriProResult indicador(String codigo, String titulo, long valor, String descricao, String status) {
        return new IndicadorNutriProResult(codigo, titulo, valor, descricao, status);
    }

    private List<AtalhoNutriProResult> atalhosPrioritarios() {
        return List.of(
                atalho("gasto-energetico", "Adicionar gasto energético", "Preparar TMB, GEB e GET do paciente na próxima etapa.", "PLANEJADO_R10", "nutri-pro/gasto-energetico"),
                atalho("exames-laboratoriais", "Adicionar exames laboratoriais", "Criar solicitação e histórico de exames em documento profissional.", "PLANEJADO_R10", "nutri-pro/exames"),
                atalho("plano-alimentar", "Adicionar plano alimentar", "Iniciar plano alimentar com refeições e observações por paciente.", "PLANEJADO_R10", "nutri-pro/plano-alimentar")
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
                acaoProntuario("gasto-energetico", "Adicionar gasto energético", "Abrir entrada preparada para TMB, GEB, GET e objetivo energético.", StatusAcaoNutriPro.PROXIMA_TASK, true),
                acaoProntuario("exames-laboratoriais", "Adicionar exames laboratoriais", "Preparar solicitação de exames e histórico do paciente.", StatusAcaoNutriPro.PREPARADO, true),
                acaoProntuario("plano-alimentar", "Adicionar plano alimentar", "Iniciar fluxo preparado para plano alimentar por paciente.", StatusAcaoNutriPro.PREPARADO, true),
                acaoProntuario("avaliacao-antropometrica", "Adicionar avaliação antropométrica", "Abrir estado preparado para peso, altura, IMC e objetivo.", StatusAcaoNutriPro.PROXIMA_TASK, false),
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

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_VERTICAIS);
        permissaoAcessoService.validarPermissao(PermissaoAcesso.VISUALIZAR_EMPRESA);
    }
}
