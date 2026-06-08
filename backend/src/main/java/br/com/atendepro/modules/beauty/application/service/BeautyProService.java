package br.com.atendepro.modules.beauty.application.service;

import java.math.BigDecimal;
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
import br.com.atendepro.modules.beauty.application.command.ConsultarIntegracoesOperacionaisBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ConsultarProntuarioBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ConsultarSegurancaOperacionalBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ConsultarVisaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarEvidenciaEvolucaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarFichaEsteticaBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarProtocoloBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarTermoConsentimentoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.DetalharProtocoloBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ListarClientesBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ListarFichasEsteticasBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ListarProtocolosBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.RegistrarSessaoProtocoloBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.VincularProdutoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.port.in.AtualizarFichaEsteticaBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ConsultarIntegracoesOperacionaisBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ConsultarProntuarioBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ConsultarSegurancaOperacionalBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ConsultarVisaoBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.CriarEvidenciaEvolucaoBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.CriarFichaEsteticaBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.CriarProtocoloBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.CriarTermoConsentimentoBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.DetalharProtocoloBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ListarClientesBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ListarFichasEsteticasBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ListarProtocolosBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.RegistrarSessaoProtocoloBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.VincularProdutoBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.out.AtualizarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.AtualizarProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.BaixarProdutoEstoqueBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarClienteBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarIntegracoesOperacionaisBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarVisaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarEvidenciasEvolucaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.result.AtalhoBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;
import br.com.atendepro.modules.beauty.application.result.EvidenciaEvolucaoBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.FichaEsteticaBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.IndicadorBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.IntegracoesOperacionaisBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.MetricasBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ProdutoBeautyEstoqueResult;
import br.com.atendepro.modules.beauty.application.result.ProdutoUtilizadoBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ProtocoloBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ProntuarioBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ResumoProntuarioBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.SegurancaOperacionalBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.SessaoProtocoloBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.TermoConsentimentoBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.VisaoBeautyProResult;
import br.com.atendepro.modules.beauty.application.port.out.ListarClientesBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarFichasEsteticasBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarProdutosEstoqueBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarProdutosUtilizadosBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarProtocolosBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarSessoesProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarTermosConsentimentoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarEvidenciaEvolucaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarProdutoUtilizadoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarSessaoProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.SalvarTermoConsentimentoBeautyProPort;
import br.com.atendepro.modules.beauty.domain.model.EvidenciaEvolucaoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.FichaEsteticaBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.ProdutoUtilizadoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.ProtocoloBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.SessaoProtocoloBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.StatusOperacionalBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TermoConsentimentoBeautyPro;
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
        ListarFichasEsteticasBeautyProUseCase,
        CriarProtocoloBeautyProUseCase,
        ListarProtocolosBeautyProUseCase,
        DetalharProtocoloBeautyProUseCase,
        RegistrarSessaoProtocoloBeautyProUseCase,
        ConsultarSegurancaOperacionalBeautyProUseCase,
        CriarTermoConsentimentoBeautyProUseCase,
        CriarEvidenciaEvolucaoBeautyProUseCase,
        VincularProdutoBeautyProUseCase,
        ConsultarIntegracoesOperacionaisBeautyProUseCase {

    private final CarregarVisaoBeautyProPort carregarVisaoBeautyProPort;
    private final ListarClientesBeautyProPort listarClientesBeautyProPort;
    private final CarregarClienteBeautyProPort carregarClienteBeautyProPort;
    private final CarregarFichaEsteticaBeautyProPort carregarFichaEsteticaBeautyProPort;
    private final SalvarFichaEsteticaBeautyProPort salvarFichaEsteticaBeautyProPort;
    private final AtualizarFichaEsteticaBeautyProPort atualizarFichaEsteticaBeautyProPort;
    private final ListarFichasEsteticasBeautyProPort listarFichasEsteticasBeautyProPort;
    private final SalvarProtocoloBeautyProPort salvarProtocoloBeautyProPort;
    private final AtualizarProtocoloBeautyProPort atualizarProtocoloBeautyProPort;
    private final CarregarProtocoloBeautyProPort carregarProtocoloBeautyProPort;
    private final ListarProtocolosBeautyProPort listarProtocolosBeautyProPort;
    private final SalvarSessaoProtocoloBeautyProPort salvarSessaoProtocoloBeautyProPort;
    private final ListarSessoesProtocoloBeautyProPort listarSessoesProtocoloBeautyProPort;
    private final SalvarTermoConsentimentoBeautyProPort salvarTermoConsentimentoBeautyProPort;
    private final ListarTermosConsentimentoBeautyProPort listarTermosConsentimentoBeautyProPort;
    private final SalvarEvidenciaEvolucaoBeautyProPort salvarEvidenciaEvolucaoBeautyProPort;
    private final ListarEvidenciasEvolucaoBeautyProPort listarEvidenciasEvolucaoBeautyProPort;
    private final SalvarProdutoUtilizadoBeautyProPort salvarProdutoUtilizadoBeautyProPort;
    private final ListarProdutosUtilizadosBeautyProPort listarProdutosUtilizadosBeautyProPort;
    private final ListarProdutosEstoqueBeautyProPort listarProdutosEstoqueBeautyProPort;
    private final BaixarProdutoEstoqueBeautyProPort baixarProdutoEstoqueBeautyProPort;
    private final CarregarIntegracoesOperacionaisBeautyProPort carregarIntegracoesOperacionaisBeautyProPort;
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
            SalvarProtocoloBeautyProPort salvarProtocoloBeautyProPort,
            AtualizarProtocoloBeautyProPort atualizarProtocoloBeautyProPort,
            CarregarProtocoloBeautyProPort carregarProtocoloBeautyProPort,
            ListarProtocolosBeautyProPort listarProtocolosBeautyProPort,
            SalvarSessaoProtocoloBeautyProPort salvarSessaoProtocoloBeautyProPort,
            ListarSessoesProtocoloBeautyProPort listarSessoesProtocoloBeautyProPort,
            SalvarTermoConsentimentoBeautyProPort salvarTermoConsentimentoBeautyProPort,
            ListarTermosConsentimentoBeautyProPort listarTermosConsentimentoBeautyProPort,
            SalvarEvidenciaEvolucaoBeautyProPort salvarEvidenciaEvolucaoBeautyProPort,
            ListarEvidenciasEvolucaoBeautyProPort listarEvidenciasEvolucaoBeautyProPort,
            SalvarProdutoUtilizadoBeautyProPort salvarProdutoUtilizadoBeautyProPort,
            ListarProdutosUtilizadosBeautyProPort listarProdutosUtilizadosBeautyProPort,
            ListarProdutosEstoqueBeautyProPort listarProdutosEstoqueBeautyProPort,
            BaixarProdutoEstoqueBeautyProPort baixarProdutoEstoqueBeautyProPort,
            CarregarIntegracoesOperacionaisBeautyProPort carregarIntegracoesOperacionaisBeautyProPort,
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
        this.salvarProtocoloBeautyProPort = salvarProtocoloBeautyProPort;
        this.atualizarProtocoloBeautyProPort = atualizarProtocoloBeautyProPort;
        this.carregarProtocoloBeautyProPort = carregarProtocoloBeautyProPort;
        this.listarProtocolosBeautyProPort = listarProtocolosBeautyProPort;
        this.salvarSessaoProtocoloBeautyProPort = salvarSessaoProtocoloBeautyProPort;
        this.listarSessoesProtocoloBeautyProPort = listarSessoesProtocoloBeautyProPort;
        this.salvarTermoConsentimentoBeautyProPort = salvarTermoConsentimentoBeautyProPort;
        this.listarTermosConsentimentoBeautyProPort = listarTermosConsentimentoBeautyProPort;
        this.salvarEvidenciaEvolucaoBeautyProPort = salvarEvidenciaEvolucaoBeautyProPort;
        this.listarEvidenciasEvolucaoBeautyProPort = listarEvidenciasEvolucaoBeautyProPort;
        this.salvarProdutoUtilizadoBeautyProPort = salvarProdutoUtilizadoBeautyProPort;
        this.listarProdutosUtilizadosBeautyProPort = listarProdutosUtilizadosBeautyProPort;
        this.listarProdutosEstoqueBeautyProPort = listarProdutosEstoqueBeautyProPort;
        this.baixarProdutoEstoqueBeautyProPort = baixarProdutoEstoqueBeautyProPort;
        this.carregarIntegracoesOperacionaisBeautyProPort = carregarIntegracoesOperacionaisBeautyProPort;
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
    public IntegracoesOperacionaisBeautyProResult consultarIntegracoesOperacionais(ConsultarIntegracoesOperacionaisBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        return carregarIntegracoesOperacionaisBeautyProPort.carregarIntegracoesOperacionais(empresaId, LocalDate.now(clock));
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

    @Override
    public ProtocoloBeautyProResult criarProtocolo(CriarProtocoloBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        ProtocoloBeautyPro protocolo = ProtocoloBeautyPro.criar(
                empresaId,
                command.clienteId(),
                command.servicoProcedimentoId(),
                command.nome(),
                command.tipo(),
                command.objetivo(),
                command.quantidadeSessoesPrevistas(),
                command.observacoes(),
                Instant.now(clock)
        );
        salvarProtocoloBeautyProPort.salvarProtocolo(protocolo);
        return ProtocoloBeautyProResult.de(protocolo, List.of());
    }

    @Override
    public List<ProtocoloBeautyProResult> listarProtocolos(ListarProtocolosBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        return listarProtocolosBeautyProPort
                .listarProtocolos(empresaId, command.clienteId())
                .stream()
                .map(protocolo -> ProtocoloBeautyProResult.de(
                        protocolo,
                        listarSessoesProtocoloBeautyProPort.listarSessoes(empresaId, protocolo.id())
                ))
                .toList();
    }

    @Override
    public Optional<ProtocoloBeautyProResult> detalharProtocolo(DetalharProtocoloBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        return carregarProtocoloBeautyProPort
                .carregarProtocolo(empresaId, command.clienteId(), command.protocoloId())
                .map(protocolo -> ProtocoloBeautyProResult.de(
                        protocolo,
                        listarSessoesProtocoloBeautyProPort.listarSessoes(empresaId, protocolo.id())
                ));
    }

    @Override
    public Optional<SessaoProtocoloBeautyProResult> registrarSessao(RegistrarSessaoProtocoloBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        return carregarProtocoloBeautyProPort
                .carregarProtocolo(empresaId, command.clienteId(), command.protocoloId())
                .map(protocolo -> {
                    ProtocoloBeautyPro protocoloAtualizado = protocolo.registrarSessao(Instant.now(clock));
                    SessaoProtocoloBeautyPro sessao = SessaoProtocoloBeautyPro.criar(
                            empresaId,
                            protocolo.id(),
                            command.clienteId(),
                            command.agendaCompromissoId(),
                            protocoloAtualizado.sessoesRealizadas(),
                            command.realizadaEm() == null ? Instant.now(clock) : command.realizadaEm(),
                            command.descricaoExecucao(),
                            command.evolucaoCliente(),
                            command.produtosUtilizados(),
                            command.orientacoes(),
                            Instant.now(clock)
                    );
                    salvarSessaoProtocoloBeautyProPort.salvarSessao(sessao);
                    atualizarProtocoloBeautyProPort.atualizarProtocolo(protocoloAtualizado);
                    return SessaoProtocoloBeautyProResult.de(sessao);
                });
    }

    @Override
    public SegurancaOperacionalBeautyProResult consultarSegurancaOperacional(ConsultarSegurancaOperacionalBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        LocalDate hoje = LocalDate.now(clock);
        return new SegurancaOperacionalBeautyProResult(
                listarTermosConsentimentoBeautyProPort.listarTermosConsentimento(empresaId, command.clienteId())
                        .stream()
                        .map(TermoConsentimentoBeautyProResult::de)
                        .toList(),
                listarEvidenciasEvolucaoBeautyProPort.listarEvidenciasEvolucao(empresaId, command.clienteId())
                        .stream()
                        .map(EvidenciaEvolucaoBeautyProResult::de)
                        .toList(),
                listarProdutosUtilizadosBeautyProPort.listarProdutosUtilizados(empresaId, command.clienteId())
                        .stream()
                        .map(ProdutoUtilizadoBeautyProResult::de)
                        .toList(),
                listarProdutosEstoqueBeautyProPort.listarProdutosEstoqueBeauty(empresaId, hoje)
        );
    }

    @Override
    public TermoConsentimentoBeautyProResult criarTermoConsentimento(CriarTermoConsentimentoBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        validarProtocoloBeautySeInformado(empresaId, command.clienteId(), command.protocoloId());
        TermoConsentimentoBeautyPro termo = TermoConsentimentoBeautyPro.criar(
                empresaId,
                command.clienteId(),
                command.protocoloId(),
                command.titulo(),
                command.conteudo(),
                command.aceiteProfissional(),
                Instant.now(clock)
        );
        salvarTermoConsentimentoBeautyProPort.salvarTermoConsentimento(termo);
        return TermoConsentimentoBeautyProResult.de(termo);
    }

    @Override
    public EvidenciaEvolucaoBeautyProResult criarEvidenciaEvolucao(CriarEvidenciaEvolucaoBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        validarProtocoloBeautySeInformado(empresaId, command.clienteId(), command.protocoloId());
        validarSessaoBeautySeInformada(empresaId, command.protocoloId(), command.sessaoId());
        EvidenciaEvolucaoBeautyPro evidencia = EvidenciaEvolucaoBeautyPro.criar(
                empresaId,
                command.clienteId(),
                command.protocoloId(),
                command.sessaoId(),
                command.tipoPlaceholder(),
                command.titulo(),
                command.descricao(),
                command.observacoesPrivacidade(),
                Instant.now(clock)
        );
        salvarEvidenciaEvolucaoBeautyProPort.salvarEvidenciaEvolucao(evidencia);
        return EvidenciaEvolucaoBeautyProResult.de(evidencia);
    }

    @Override
    public ProdutoUtilizadoBeautyProResult vincularProduto(VincularProdutoBeautyProCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        validarClienteBeautyExiste(empresaId, command.clienteId());
        validarProtocoloBeautySeInformado(empresaId, command.clienteId(), command.protocoloId());
        validarSessaoBeautySeInformada(empresaId, command.protocoloId(), command.sessaoId());
        LocalDate hoje = LocalDate.now(clock);
        Instant agora = Instant.now(clock);
        ProdutoBeautyEstoqueResult produtoEstoque = resolverProdutoEstoque(empresaId, command.produtoEstoqueId(), hoje);
        boolean estoqueBaixoAposUso = produtoEstoque != null && estoqueBaixoAposUso(produtoEstoque, command.quantidade());
        ProdutoUtilizadoBeautyPro produto = ProdutoUtilizadoBeautyPro.vincular(
                empresaId,
                command.clienteId(),
                command.protocoloId(),
                command.sessaoId(),
                command.produtoEstoqueId(),
                produtoEstoque == null ? command.nomeProduto() : produtoEstoque.nome(),
                produtoEstoque == null ? command.lote() : produtoEstoque.lote(),
                produtoEstoque == null ? command.validade() : produtoEstoque.validade(),
                command.quantidade(),
                produtoEstoque == null ? command.unidade() : produtoEstoque.unidade(),
                estoqueBaixoAposUso,
                command.observacoes(),
                hoje,
                agora
        );
        salvarProdutoUtilizadoBeautyProPort.salvarProdutoUtilizado(produto);
        if (produtoEstoque != null) {
            baixarProdutoEstoqueBeautyProPort.baixarProdutoEstoqueBeauty(empresaId, produtoEstoque.id(), command.quantidade(), agora);
        }
        return ProdutoUtilizadoBeautyProResult.de(produto);
    }

    private boolean estoqueBaixoAposUso(ProdutoBeautyEstoqueResult produtoEstoque, BigDecimal quantidadeUsada) {
        if (quantidadeUsada == null || quantidadeUsada.signum() <= 0) {
            throw new BusinessException(
                    "BEAUTY_QUANTIDADE_PRODUTO_INVALIDA",
                    "Quantidade do produto Beauty deve ser positiva."
            );
        }
        BigDecimal saldoAposUso = produtoEstoque.quantidadeAtual().subtract(quantidadeUsada);
        if (saldoAposUso.signum() < 0) {
            throw new BusinessException(
                    "BEAUTY_ESTOQUE_INSUFICIENTE",
                    "Quantidade informada excede o saldo disponivel do produto."
            );
        }
        return saldoAposUso.compareTo(produtoEstoque.estoqueMinimo()) <= 0;
    }

    private List<IndicadorBeautyProResult> indicadores(MetricasBeautyProResult metricas) {
        return List.of(
                indicador("clientes", "Clientes Beauty", metricas.clientesAtivos(), "Clientes ativos da área de estética, beleza e salão.", "OPERACIONAL"),
                indicador("agendaHoje", "Agenda hoje", metricas.agendaHoje(), "Atendimentos Beauty previstos para hoje.", "OPERACIONAL"),
                indicador("agenda7Dias", "Próximos 7 dias", metricas.agendaProximos7Dias(), "Sessões, retornos e procedimentos da semana.", "OPERACIONAL"),
                indicador("servicos", "Serviços Beauty", metricas.servicosBeautyAtivos(), "Procedimentos e serviços ativos da vertical.", "OPERACIONAL"),
                indicador("protocolos", "Protocolos ativos", metricas.protocolosAtivos(), "Pacotes e protocolos estéticos em acompanhamento.", "OPERACIONAL"),
                indicador("sessoes", "Sessões realizadas", metricas.sessoesRealizadas(), "Evoluções registradas nos protocolos Beauty.", "OPERACIONAL"),
                indicador("termos", "Termos gerados", metricas.termosConsentimento(), "Consentimentos e orientações registrados no histórico do cliente.", "OPERACIONAL"),
                indicador("evidencias", "Evidências seguras", metricas.evidenciasSeguras(), "Registros de evolução sem armazenamento de fotos reais.", "OPERACIONAL"),
                indicador("produtos", "Produtos ativos", metricas.produtosAtivos(), "Produtos e insumos disponíveis para protocolos.", "OPERACIONAL"),
                indicador("produtosVinculados", "Produtos rastreados", metricas.produtosVinculados(), "Produtos e lotes vinculados aos protocolos e sessões.", "OPERACIONAL"),
                indicador("alertasProdutos", "Alertas de produtos", metricas.alertasProdutos(), "Produtos vinculados com validade próxima ou estoque baixo.", metricas.alertasProdutos() > 0 ? "ALERTA" : "SAUDAVEL"),
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
                atalho("termos", "Termos e documentos", "Gerar consentimentos, orientações e registros profissionais no histórico.", "DISPONIVEL", "beauty-pro/termos")
        );
    }

    private List<AtalhoBeautyProResult> proximasEvolucoes() {
        return List.of(
                atalho("produtos", "Produtos e lotes", "Conectar produtos, validade, lotes e insumos aos procedimentos.", "DISPONIVEL", "beauty-pro/produtos"),
                atalho("fotos-placeholder", "Evolução visual segura", "Registrar evidências e placeholders sem usar fotos reais de pessoas.", "DISPONIVEL", "beauty-pro/evolucao"),
                atalho("dashboard", "Dashboard Beauty Pro", "Acompanhar indicadores por protocolo, sessão, produto e precificação.", "DISPONIVEL", "beauty-pro/dashboard")
        );
    }

    private AtalhoBeautyProResult atalho(String codigo, String titulo, String descricao, String status, String destino) {
        return new AtalhoBeautyProResult(codigo, titulo, descricao, status, destino);
    }

    private ProdutoBeautyEstoqueResult resolverProdutoEstoque(UUID empresaId, UUID produtoEstoqueId, LocalDate hoje) {
        if (produtoEstoqueId == null) {
            return null;
        }
        return listarProdutosEstoqueBeautyProPort.listarProdutosEstoqueBeauty(empresaId, hoje)
                .stream()
                .filter(produto -> produto.id().equals(produtoEstoqueId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "BEAUTY_PRODUTO_ESTOQUE_NAO_ENCONTRADO",
                        "Produto de estoque nao encontrado para esta empresa."
                ));
    }

    private void validarProtocoloBeautySeInformado(UUID empresaId, UUID clienteId, UUID protocoloId) {
        if (protocoloId == null) {
            return;
        }
        boolean existe = carregarProtocoloBeautyProPort
                .carregarProtocolo(empresaId, clienteId, protocoloId)
                .isPresent();
        if (!existe) {
            throw new BusinessException("BEAUTY_PRO_PROTOCOLO_NAO_ENCONTRADO", "Protocolo Beauty nao encontrado para este cliente.");
        }
    }

    private void validarSessaoBeautySeInformada(UUID empresaId, UUID protocoloId, UUID sessaoId) {
        if (sessaoId == null) {
            return;
        }
        if (protocoloId == null) {
            throw new BusinessException("BEAUTY_PRO_PROTOCOLO_OBRIGATORIO", "Protocolo e obrigatorio ao vincular sessao Beauty.");
        }
        boolean existe = listarSessoesProtocoloBeautyProPort
                .listarSessoes(empresaId, protocoloId)
                .stream()
                .anyMatch(sessao -> sessao.id().equals(sessaoId));
        if (!existe) {
            throw new BusinessException("BEAUTY_PRO_SESSAO_NAO_ENCONTRADA", "Sessao Beauty nao encontrada para este protocolo.");
        }
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
