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
import br.com.atendepro.modules.nutri.application.port.in.ConsultarVisaoNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.out.CarregarVisaoNutriProPort;
import br.com.atendepro.modules.nutri.application.result.AtalhoNutriProResult;
import br.com.atendepro.modules.nutri.application.result.IndicadorNutriProResult;
import br.com.atendepro.modules.nutri.application.result.MetricasNutriProResult;
import br.com.atendepro.modules.nutri.application.result.VisaoNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.StatusOperacionalNutriPro;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class NutriProService implements ConsultarVisaoNutriProUseCase {

    private final CarregarVisaoNutriProPort carregarVisaoNutriProPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public NutriProService(
            CarregarVisaoNutriProPort carregarVisaoNutriProPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarVisaoNutriProPort = carregarVisaoNutriProPort;
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

    private List<IndicadorNutriProResult> indicadores(MetricasNutriProResult metricas) {
        return List.of(
                indicador("pacientes", "Pacientes Nutri", metricas.pacientesAtivos(), "Pacientes ativos da area de nutricao.", "OPERACIONAL"),
                indicador("agendaHoje", "Agenda hoje", metricas.agendaHoje(), "Atendimentos nutricionais previstos para hoje.", "OPERACIONAL"),
                indicador("agenda7Dias", "Proximos 7 dias", metricas.agendaProximos7Dias(), "Consultas e retornos nutricionais da semana.", "OPERACIONAL"),
                indicador("servicos", "Servicos Nutri", metricas.servicosNutriAtivos(), "Procedimentos e servicos ativos da vertical.", "OPERACIONAL"),
                indicador("documentos", "Documentos", metricas.documentosNutri(), "Documentos nutricionais vinculados a pacientes.", "PREPARADO"),
                indicador("precificacao", "Precificacao", metricas.simulacoesPrecificacao(), "Simulacoes de custo real para servicos de nutricao.", "OPERACIONAL"),
                indicador("alertas", "Alertas de preco", metricas.simulacoesEmAlerta(), "Simulacoes com margem baixa ou prejuizo.", metricas.simulacoesEmAlerta() > 0 ? "ALERTA" : "SAUDAVEL"),
                indicador("planos", "Planos alimentares", metricas.planosAlimentaresAtivos(), "Reservado para a task de plano alimentar.", "PLANEJADO")
        );
    }

    private IndicadorNutriProResult indicador(String codigo, String titulo, long valor, String descricao, String status) {
        return new IndicadorNutriProResult(codigo, titulo, valor, descricao, status);
    }

    private List<AtalhoNutriProResult> atalhosPrioritarios() {
        return List.of(
                atalho("gasto-energetico", "Adicionar gasto energetico", "Preparar TMB, GEB e GET do paciente na proxima etapa.", "PLANEJADO_R10", "nutri-pro/gasto-energetico"),
                atalho("exames-laboratoriais", "Adicionar exames laboratoriais", "Criar solicitacao e historico de exames em documento profissional.", "PLANEJADO_R10", "nutri-pro/exames"),
                atalho("plano-alimentar", "Adicionar plano alimentar", "Iniciar plano alimentar com refeicoes e observacoes por paciente.", "PLANEJADO_R10", "nutri-pro/plano-alimentar")
        );
    }

    private List<AtalhoNutriProResult> proximasEvolucoes() {
        return List.of(
                atalho("prontuario", "Prontuario nutricional", "Perfil nutricional com resumo, historico e menu rapido funcional.", "PROXIMA_TASK", "nutri-pro/prontuario"),
                atalho("avaliacao", "Avaliacao antropometrica", "Peso, altura, IMC, objetivos e evolucao corporal.", "PLANEJADO_R10", "nutri-pro/avaliacao"),
                atalho("documentos", "Documentos com CRN", "Solicitacoes, prescricoes e carimbo profissional.", "PLANEJADO_R10", "nutri-pro/documentos")
        );
    }

    private AtalhoNutriProResult atalho(String codigo, String titulo, String descricao, String status, String destino) {
        return new AtalhoNutriProResult(codigo, titulo, descricao, status, destino);
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
