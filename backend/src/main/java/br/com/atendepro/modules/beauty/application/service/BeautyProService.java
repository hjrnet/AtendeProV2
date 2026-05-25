package br.com.atendepro.modules.beauty.application.service;

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
import br.com.atendepro.modules.beauty.application.command.ConsultarVisaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.port.in.ConsultarVisaoBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.out.CarregarVisaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.result.AtalhoBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.IndicadorBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.MetricasBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.VisaoBeautyProResult;
import br.com.atendepro.modules.beauty.domain.model.StatusOperacionalBeautyPro;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class BeautyProService implements ConsultarVisaoBeautyProUseCase {

    private final CarregarVisaoBeautyProPort carregarVisaoBeautyProPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public BeautyProService(
            CarregarVisaoBeautyProPort carregarVisaoBeautyProPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarVisaoBeautyProPort = carregarVisaoBeautyProPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public VisaoBeautyProResult consultarVisaoBeautyPro(ConsultarVisaoBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        MetricasBeautyProResult metricas = carregarVisaoBeautyProPort.carregarVisaoBeautyPro(
                empresaId,
                LocalDate.now(clock)
        );
        StatusOperacionalBeautyPro status = StatusOperacionalBeautyPro.definir(
                metricas.clientesAtivos(),
                metricas.servicosBeautyAtivos()
        );
        return new VisaoBeautyProResult(
                empresaId,
                metricas.empresaNome(),
                status,
                indicadores(metricas),
                atalhosPrioritarios(),
                proximasEvolucoes(),
                metricas.clientesRecentes(),
                Instant.now(clock)
        );
    }

    private List<IndicadorBeautyProResult> indicadores(MetricasBeautyProResult metricas) {
        return List.of(
                indicador("clientes", "Clientes Beauty", metricas.clientesAtivos(), "Clientes ativos da área de estética, beleza e salão.", "OPERACIONAL"),
                indicador("agendaHoje", "Agenda hoje", metricas.agendaHoje(), "Atendimentos Beauty previstos para hoje.", "OPERACIONAL"),
                indicador("agenda7Dias", "Próximos 7 dias", metricas.agendaProximos7Dias(), "Sessões, retornos e procedimentos da semana.", "OPERACIONAL"),
                indicador("servicos", "Serviços Beauty", metricas.servicosBeautyAtivos(), "Procedimentos e serviços ativos da vertical.", "OPERACIONAL"),
                indicador("produtos", "Produtos ativos", metricas.produtosAtivos(), "Produtos e insumos disponíveis para protocolos.", "OPERACIONAL"),
                indicador("equipamentos", "Equipamentos", metricas.equipamentosAtivos(), "Equipamentos ativos para procedimentos e manutenção.", "OPERACIONAL"),
                indicador("precificacao", "Precificação", metricas.simulacoesPrecificacao(), "Simulações de custo real para serviços Beauty.", "OPERACIONAL"),
                indicador("alertas", "Alertas de preço", metricas.simulacoesEmAlerta(), "Simulações com margem baixa ou prejuízo.", metricas.simulacoesEmAlerta() > 0 ? "ALERTA" : "SAUDAVEL")
        );
    }

    private IndicadorBeautyProResult indicador(String codigo, String titulo, long valor, String descricao, String status) {
        return new IndicadorBeautyProResult(codigo, titulo, valor, descricao, status);
    }

    private List<AtalhoBeautyProResult> atalhosPrioritarios() {
        return List.of(
                atalho("ficha-estetica", "Ficha estética", "Preparar ficha, anamnese, objetivos, contraindicações e avaliação inicial.", "PROXIMA_TASK", "beauty-pro/ficha"),
                atalho("protocolos", "Protocolos e sessões", "Preparar protocolos faciais/corporais, pacote de sessões e evolução.", "PLANEJADO_R10", "beauty-pro/protocolos"),
                atalho("termos", "Termos e documentos", "Preparar consentimentos, orientações e registros profissionais.", "PLANEJADO_R10", "beauty-pro/termos")
        );
    }

    private List<AtalhoBeautyProResult> proximasEvolucoes() {
        return List.of(
                atalho("produtos", "Produtos e lotes", "Conectar produtos, validade, lotes e insumos aos procedimentos.", "PLANEJADO_R10", "beauty-pro/produtos"),
                atalho("fotos-placeholder", "Evolução visual segura", "Preparar evidências e placeholders sem usar fotos reais de pessoas.", "PLANEJADO_R10", "beauty-pro/evolucao"),
                atalho("dashboard", "Dashboard Beauty Pro", "Evoluir indicadores por protocolo, sessão, retorno, produto e precificação.", "PLANEJADO_R10", "beauty-pro/dashboard")
        );
    }

    private AtalhoBeautyProResult atalho(String codigo, String titulo, String descricao, String status, String destino) {
        return new AtalhoBeautyProResult(codigo, titulo, descricao, status, destino);
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
            throw new BusinessException("BEAUTY_PRO_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar Beauty Pro.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_VERTICAIS);
    }
}
