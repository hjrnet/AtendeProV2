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
import br.com.atendepro.modules.beauty.application.command.AtualizarFichaEsteticaBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ConsultarProntuarioBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ConsultarVisaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarFichaEsteticaBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ListarClientesBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ListarFichasEsteticasBeautyProCommand;
import br.com.atendepro.modules.beauty.application.port.in.AtualizarFichaEsteticaBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ConsultarProntuarioBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ConsultarVisaoBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.CriarFichaEsteticaBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ListarClientesBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ListarFichasEsteticasBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.out.AtualizarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarClienteBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarVisaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.result.AtalhoBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;
import br.com.atendepro.modules.beauty.application.result.FichaEsteticaBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.IndicadorBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.MetricasBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ProntuarioBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ResumoProntuarioBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.VisaoBeautyProResult;
import br.com.atendepro.modules.beauty.application.port.out.ListarClientesBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarFichasEsteticasBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.domain.model.FichaEsteticaBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.StatusOperacionalBeautyPro;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class BeautyProService implements
        ConsultarVisaoBeautyProUseCase,
        ListarClientesBeautyProUseCase,
        ConsultarProntuarioBeautyProUseCase,
        CriarFichaEsteticaBeautyProUseCase,
        AtualizarFichaEsteticaBeautyProUseCase,
        ListarFichasEsteticasBeautyProUseCase {

    private final CarregarVisaoBeautyProPort carregarVisaoBeautyProPort;
    private final ListarClientesBeautyProPort listarClientesBeautyProPort;
    private final CarregarClienteBeautyProPort carregarClienteBeautyProPort;
    private final CarregarFichaEsteticaBeautyProPort carregarFichaEsteticaBeautyProPort;
    private final SalvarFichaEsteticaBeautyProPort salvarFichaEsteticaBeautyProPort;
    private final AtualizarFichaEsteticaBeautyProPort atualizarFichaEsteticaBeautyProPort;
    private final ListarFichasEsteticasBeautyProPort listarFichasEsteticasBeautyProPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public BeautyProService(
            CarregarVisaoBeautyProPort carregarVisaoBeautyProPort,
            ListarClientesBeautyProPort listarClientesBeautyProPort,
            CarregarClienteBeautyProPort carregarClienteBeautyProPort,
            CarregarFichaEsteticaBeautyProPort carregarFichaEsteticaBeautyProPort,
            SalvarFichaEsteticaBeautyProPort salvarFichaEsteticaBeautyProPort,
            AtualizarFichaEsteticaBeautyProPort atualizarFichaEsteticaBeautyProPort,
            ListarFichasEsteticasBeautyProPort listarFichasEsteticasBeautyProPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarVisaoBeautyProPort = carregarVisaoBeautyProPort;
        this.listarClientesBeautyProPort = listarClientesBeautyProPort;
        this.carregarClienteBeautyProPort = carregarClienteBeautyProPort;
        this.carregarFichaEsteticaBeautyProPort = carregarFichaEsteticaBeautyProPort;
        this.salvarFichaEsteticaBeautyProPort = salvarFichaEsteticaBeautyProPort;
        this.atualizarFichaEsteticaBeautyProPort = atualizarFichaEsteticaBeautyProPort;
        this.listarFichasEsteticasBeautyProPort = listarFichasEsteticasBeautyProPort;
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

    @Override
    public List<ClienteBeautyResumoResult> listarClientesBeautyPro(ListarClientesBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        return listarClientesBeautyProPort.listarClientesBeautyPro(empresaId, command.busca());
    }

    @Override
    public Optional<ProntuarioBeautyProResult> consultarProntuarioBeautyPro(ConsultarProntuarioBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        return carregarClienteBeautyProPort
                .carregarClienteBeautyPro(empresaId, command.clienteId(), LocalDate.now(clock))
                .map(cliente -> {
                    FichaEsteticaBeautyPro fichaAtual = carregarFichaEsteticaBeautyProPort
                            .carregarFichaAtual(empresaId, command.clienteId())
                            .orElse(null);
                    long fichas = listarFichasEsteticasBeautyProPort.listarFichasEsteticas(empresaId, command.clienteId()).size();
                    return new ProntuarioBeautyProResult(
                            cliente,
                            new ResumoProntuarioBeautyProResult(
                                    fichas,
                                    0,
                                    0,
                                    fichaAtual == null ? "PENDENTE" : "DISPONIVEL",
                                    fichaAtual != null && fichaAtual.possuiAlertaContraindicacao() ? "ALERTA" : "SEM_ALERTA",
                                    null
                            ),
                            fichaAtual == null ? null : FichaEsteticaBeautyProResult.de(fichaAtual)
                    );
                });
    }

    @Override
    public FichaEsteticaBeautyProResult criarFichaEstetica(CriarFichaEsteticaBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        FichaEsteticaBeautyPro ficha = FichaEsteticaBeautyPro.criar(
                empresaId,
                command.clienteId(),
                command.objetivo(),
                command.queixaPrincipal(),
                command.historicoEstetico(),
                command.alergias(),
                command.medicamentos(),
                command.gestante(),
                command.lactante(),
                command.sensibilidadePele(),
                command.usaAcidos(),
                command.exposicaoSolarIntensa(),
                command.procedimentosRecentes(),
                command.contraindicacoes(),
                command.observacoes(),
                Instant.now(clock)
        );
        salvarFichaEsteticaBeautyProPort.salvarFichaEstetica(ficha);
        return FichaEsteticaBeautyProResult.de(ficha);
    }

    @Override
    public Optional<FichaEsteticaBeautyProResult> atualizarFichaEstetica(AtualizarFichaEsteticaBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        return carregarFichaEsteticaBeautyProPort
                .carregarFichaEstetica(empresaId, command.clienteId(), command.fichaId())
                .map(ficha -> ficha.atualizar(
                        command.objetivo(),
                        command.queixaPrincipal(),
                        command.historicoEstetico(),
                        command.alergias(),
                        command.medicamentos(),
                        command.gestante(),
                        command.lactante(),
                        command.sensibilidadePele(),
                        command.usaAcidos(),
                        command.exposicaoSolarIntensa(),
                        command.procedimentosRecentes(),
                        command.contraindicacoes(),
                        command.observacoes(),
                        Instant.now(clock)
                ))
                .map(fichaAtualizada -> {
                    atualizarFichaEsteticaBeautyProPort.atualizarFichaEstetica(fichaAtualizada);
                    return FichaEsteticaBeautyProResult.de(fichaAtualizada);
                });
    }

    @Override
    public List<FichaEsteticaBeautyProResult> listarFichasEsteticas(ListarFichasEsteticasBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        return listarFichasEsteticasBeautyProPort
                .listarFichasEsteticas(empresaId, command.clienteId())
                .stream()
                .map(FichaEsteticaBeautyProResult::de)
                .toList();
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

    private void validarClienteBeautyExiste(UUID empresaId, UUID clienteId) {
        boolean existe = carregarClienteBeautyProPort
                .carregarClienteBeautyPro(empresaId, clienteId, LocalDate.now(clock))
                .isPresent();
        if (!existe) {
            throw new BusinessException("BEAUTY_PRO_CLIENTE_NAO_ENCONTRADO", "Cliente Beauty nao encontrado para esta empresa.");
        }
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_VERTICAIS);
    }
}
