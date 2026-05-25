package br.com.atendepro.modules.beauty.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.beauty.application.command.ConsultarSegurancaOperacionalBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarEvidenciaEvolucaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarFichaEsteticaBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarProtocoloBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarTermoConsentimentoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.RegistrarSessaoProtocoloBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ConsultarVisaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.VincularProdutoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.port.out.AtualizarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.AtualizarProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarClienteBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarFichaEsteticaBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarProtocoloBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.CarregarVisaoBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarClientesBeautyProPort;
import br.com.atendepro.modules.beauty.application.port.out.ListarEvidenciasEvolucaoBeautyProPort;
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
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyProntuarioResult;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;
import br.com.atendepro.modules.beauty.application.result.MetricasBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ProdutoBeautyEstoqueResult;
import br.com.atendepro.modules.beauty.domain.model.EvidenciaEvolucaoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.FichaEsteticaBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.ObjetivoEsteticoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.ProdutoUtilizadoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.ProtocoloBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.SessaoProtocoloBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.StatusPacoteBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.StatusOperacionalBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TermoConsentimentoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TipoPlaceholderEvolucaoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TipoProtocoloBeautyPro;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.domain.exception.BusinessException;

class BeautyProServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("a1ef4ac8-b9e3-4c52-934c-4cf0476a8d56");
    private static final UUID OUTRA_EMPRESA_ID = UUID.fromString("bd0c1e57-1341-46a3-a5df-5e85c0b5f2ba");
    private static final UUID CLIENTE_ID = UUID.fromString("0183a2a8-bf74-4e4b-b1f1-532876a422d1");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T10:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveConsultarVisaoBeautyProNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        BeautyProService service = service(metricasComDados());

        var result = service.consultarVisaoBeautyPro(new ConsultarVisaoBeautyProCommand(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.statusOperacional()).isEqualTo(StatusOperacionalBeautyPro.OPERACIONAL);
        assertThat(result.indicadores()).extracting("codigo")
                .contains("clientes", "agendaHoje", "servicos", "produtos", "precificacao", "alertas");
        assertThat(result.atalhosPrioritarios()).extracting("codigo")
                .containsExactly("ficha-estetica", "protocolos", "termos");
        assertThat(result.clientesRecentes()).extracting("nome").containsExactly("Juliana Beauty");
    }

    @Test
    void deveExigirEmpresaParaUsuarioGlobal() {
        BeautyProService service = service(metricasComDados());

        assertThatThrownBy(() -> service.consultarVisaoBeautyPro(new ConsultarVisaoBeautyProCommand(null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa e obrigatoria para operar Beauty Pro.");
    }

    @Test
    void naoDevePermitirOutraEmpresaParaTenantRestrito() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        BeautyProService service = service(metricasComDados());

        assertThatThrownBy(() -> service.consultarVisaoBeautyPro(new ConsultarVisaoBeautyProCommand(OUTRA_EMPRESA_ID)))
                .hasMessage("Usuario nao possui acesso a esta empresa.");
    }

    @Test
    void naoDeveConsultarBeautyProSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        BeautyProService service = service(metricasComDados());

        assertThatThrownBy(() -> service.consultarVisaoBeautyPro(new ConsultarVisaoBeautyProCommand(null)))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    @Test
    void deveCriarFichaEsteticaComAlertaTextual() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        FakeBeautyPorts ports = new FakeBeautyPorts(metricasComDados());
        BeautyProService service = service(ports);

        var result = service.criarFichaEstetica(new CriarFichaEsteticaBeautyProCommand(
                null,
                CLIENTE_ID,
                ObjetivoEsteticoBeautyPro.ACNE,
                "Acne inflamada e sensibilidade",
                "Limpeza de pele recente",
                "Alergia a ácido mandélico",
                "Isotretinoína",
                false,
                false,
                true,
                true,
                true,
                "Peeling há 15 dias",
                "Evitar peeling agressivo",
                "Avaliar protocolo calmante"
        ));

        assertThat(result.possuiAlertaContraindicacao()).isTrue();
        assertThat(result.alertaContraindicacoes())
                .contains("Pele sensível")
                .contains("Uso recente de ácidos")
                .contains("Contraindicações registradas");
        assertThat(ports.fichas).hasSize(1);
    }

    @Test
    void deveConsultarProntuarioComFichaAtual() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        FakeBeautyPorts ports = new FakeBeautyPorts(metricasComDados());
        BeautyProService service = service(ports);
        service.criarFichaEstetica(new CriarFichaEsteticaBeautyProCommand(
                null,
                CLIENTE_ID,
                ObjetivoEsteticoBeautyPro.MANCHAS,
                "Manchas solares",
                null,
                null,
                null,
                false,
                false,
                false,
                false,
                true,
                null,
                null,
                null
        ));

        var result = service.consultarProntuarioBeautyPro(new br.com.atendepro.modules.beauty.application.command.ConsultarProntuarioBeautyProCommand(null, CLIENTE_ID));

        assertThat(result).isPresent();
        assertThat(result.get().fichaAtual()).isNotNull();
        assertThat(result.get().resumo().fichasEsteticas()).isEqualTo(1);
        assertThat(result.get().resumo().statusContraindicacoes()).isEqualTo("ALERTA");
    }

    @Test
    void deveCriarProtocoloERegistrarSessaoComConclusaoDoPacote() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        FakeBeautyPorts ports = new FakeBeautyPorts(metricasComDados());
        BeautyProService service = service(ports);

        var protocolo = service.criarProtocolo(new CriarProtocoloBeautyProCommand(
                null,
                CLIENTE_ID,
                null,
                "Protocolo facial clareador",
                TipoProtocoloBeautyPro.FACIAL,
                "Reduzir manchas com protocolo conservador",
                1,
                "Pacote inicial de uma sessão"
        ));
        var sessao = service.registrarSessao(new RegistrarSessaoProtocoloBeautyProCommand(
                null,
                CLIENTE_ID,
                protocolo.id(),
                null,
                Instant.parse("2026-05-25T13:00:00Z"),
                "Higienização, máscara calmante e fotoproteção",
                "Pele com boa tolerância",
                "Máscara calmante",
                "Reforçar fotoproteção"
        ));

        assertThat(sessao).isPresent();
        assertThat(sessao.get().numeroSessao()).isEqualTo(1);
        assertThat(ports.protocolos).first().extracting(ProtocoloBeautyPro::status).isEqualTo(StatusPacoteBeautyPro.CONCLUIDO);
        assertThat(ports.sessoes).hasSize(1);
    }

    @Test
    void deveCriarTermoEvidenciaSeguraEProdutoRastreado() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        FakeBeautyPorts ports = new FakeBeautyPorts(metricasComDados());
        BeautyProService service = service(ports);
        var protocolo = service.criarProtocolo(new CriarProtocoloBeautyProCommand(
                null,
                CLIENTE_ID,
                null,
                "Protocolo calmante",
                TipoProtocoloBeautyPro.FACIAL,
                "Acalmar pele sensibilizada",
                3,
                null
        ));

        var termo = service.criarTermoConsentimento(new CriarTermoConsentimentoBeautyProCommand(
                null,
                CLIENTE_ID,
                protocolo.id(),
                "Termo de consentimento facial",
                "Cliente orientada sobre cuidados e riscos do procedimento.",
                true
        ));
        var evidencia = service.criarEvidenciaEvolucao(new CriarEvidenciaEvolucaoBeautyProCommand(
                null,
                CLIENTE_ID,
                protocolo.id(),
                null,
                TipoPlaceholderEvolucaoBeautyPro.FACE_NEUTRA,
                "Evolução inicial segura",
                "Registro textual com mapa de área tratada, sem foto real.",
                "Não armazenar imagem de pessoa nesta fase."
        ));
        var produto = service.vincularProduto(new VincularProdutoBeautyProCommand(
                null,
                CLIENTE_ID,
                protocolo.id(),
                null,
                null,
                "Máscara calmante",
                "LT-2026",
                LocalDate.parse("2026-06-10"),
                new BigDecimal("1.000"),
                "UN",
                "Usada no protocolo calmante"
        ));
        var seguranca = service.consultarSegurancaOperacional(new ConsultarSegurancaOperacionalBeautyProCommand(null, CLIENTE_ID));

        assertThat(termo.statusRotulo()).isEqualTo("Aceito");
        assertThat(evidencia.avisoPrivacidade()).contains("nenhuma foto real");
        assertThat(produto.alertaValidade()).isTrue();
        assertThat(seguranca.termos()).hasSize(1);
        assertThat(seguranca.evidencias()).hasSize(1);
        assertThat(seguranca.produtosUtilizados()).hasSize(1);
    }

    private BeautyProService service(MetricasBeautyProResult metricas) {
        return service(new FakeBeautyPorts(metricas));
    }

    private BeautyProService service(FakeBeautyPorts ports) {
        return new BeautyProService(
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                ports,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private MetricasBeautyProResult metricasComDados() {
        return new MetricasBeautyProResult(
                "Studio Aesthetic Premium",
                5,
                2,
                4,
                18,
                6,
                2,
                5,
                1,
                2,
                6,
                3,
                4,
                5,
                1,
                List.of(new ClienteBeautyResumoResult(
                        CLIENTE_ID,
                        "Juliana Beauty",
                        "21999990000",
                        "Protocolo facial em acompanhamento.",
                        true,
                        Instant.parse("2026-05-25T09:00:00Z")
                ))
        );
    }

    private static class FakeBeautyPorts implements
            CarregarVisaoBeautyProPort,
            ListarClientesBeautyProPort,
            CarregarClienteBeautyProPort,
            CarregarFichaEsteticaBeautyProPort,
            SalvarFichaEsteticaBeautyProPort,
            AtualizarFichaEsteticaBeautyProPort,
            ListarFichasEsteticasBeautyProPort,
            SalvarProtocoloBeautyProPort,
            AtualizarProtocoloBeautyProPort,
            CarregarProtocoloBeautyProPort,
            ListarProtocolosBeautyProPort,
            SalvarSessaoProtocoloBeautyProPort,
            ListarSessoesProtocoloBeautyProPort,
            SalvarTermoConsentimentoBeautyProPort,
            ListarTermosConsentimentoBeautyProPort,
            SalvarEvidenciaEvolucaoBeautyProPort,
            ListarEvidenciasEvolucaoBeautyProPort,
            SalvarProdutoUtilizadoBeautyProPort,
            ListarProdutosUtilizadosBeautyProPort,
            ListarProdutosEstoqueBeautyProPort {

        private final MetricasBeautyProResult metricas;
        private final List<FichaEsteticaBeautyPro> fichas = new ArrayList<>();
        private final List<ProtocoloBeautyPro> protocolos = new ArrayList<>();
        private final List<SessaoProtocoloBeautyPro> sessoes = new ArrayList<>();
        private final List<TermoConsentimentoBeautyPro> termos = new ArrayList<>();
        private final List<EvidenciaEvolucaoBeautyPro> evidencias = new ArrayList<>();
        private final List<ProdutoUtilizadoBeautyPro> produtosUtilizados = new ArrayList<>();

        private FakeBeautyPorts(MetricasBeautyProResult metricas) {
            this.metricas = metricas;
        }

        @Override
        public MetricasBeautyProResult carregarVisaoBeautyPro(UUID empresaId, LocalDate hoje) {
            assertThat(empresaId).isEqualTo(EMPRESA_ID);
            assertThat(hoje).isEqualTo(LocalDate.parse("2026-05-25"));
            return metricas;
        }

        @Override
        public List<ClienteBeautyResumoResult> listarClientesBeautyPro(UUID empresaId, String busca) {
            assertThat(empresaId).isEqualTo(EMPRESA_ID);
            return metricas.clientesRecentes();
        }

        @Override
        public Optional<ClienteBeautyProntuarioResult> carregarClienteBeautyPro(UUID empresaId, UUID clienteId, LocalDate hoje) {
            if (!EMPRESA_ID.equals(empresaId) || !CLIENTE_ID.equals(clienteId)) {
                return Optional.empty();
            }
            return Optional.of(new ClienteBeautyProntuarioResult(
                    CLIENTE_ID,
                    EMPRESA_ID,
                    "Juliana Beauty",
                    "juliana.beauty@atendepro.local",
                    "21999990000",
                    LocalDate.parse("1990-05-20"),
                    36,
                    "Protocolo facial em acompanhamento.",
                    true,
                    Instant.parse("2026-05-25T09:00:00Z")
            ));
        }

        @Override
        public Optional<FichaEsteticaBeautyPro> carregarFichaAtual(UUID empresaId, UUID clienteId) {
            return fichas.stream().filter(ficha -> ficha.empresaId().equals(empresaId) && ficha.clienteId().equals(clienteId)).findFirst();
        }

        @Override
        public Optional<FichaEsteticaBeautyPro> carregarFichaEstetica(UUID empresaId, UUID clienteId, UUID fichaId) {
            return fichas.stream()
                    .filter(ficha -> ficha.empresaId().equals(empresaId) && ficha.clienteId().equals(clienteId) && ficha.id().equals(fichaId))
                    .findFirst();
        }

        @Override
        public void salvarFichaEstetica(FichaEsteticaBeautyPro ficha) {
            fichas.add(ficha);
        }

        @Override
        public void atualizarFichaEstetica(FichaEsteticaBeautyPro ficha) {
            fichas.removeIf(item -> item.id().equals(ficha.id()));
            fichas.add(ficha);
        }

        @Override
        public List<FichaEsteticaBeautyPro> listarFichasEsteticas(UUID empresaId, UUID clienteId) {
            return fichas.stream()
                    .filter(ficha -> ficha.empresaId().equals(empresaId) && ficha.clienteId().equals(clienteId))
                    .toList();
        }

        @Override
        public void salvarProtocolo(ProtocoloBeautyPro protocolo) {
            protocolos.add(protocolo);
        }

        @Override
        public void atualizarProtocolo(ProtocoloBeautyPro protocolo) {
            protocolos.removeIf(item -> item.id().equals(protocolo.id()));
            protocolos.add(protocolo);
        }

        @Override
        public Optional<ProtocoloBeautyPro> carregarProtocolo(UUID empresaId, UUID clienteId, UUID protocoloId) {
            return protocolos.stream()
                    .filter(protocolo -> protocolo.empresaId().equals(empresaId) && protocolo.clienteId().equals(clienteId) && protocolo.id().equals(protocoloId))
                    .findFirst();
        }

        @Override
        public List<ProtocoloBeautyPro> listarProtocolos(UUID empresaId, UUID clienteId) {
            return protocolos.stream()
                    .filter(protocolo -> protocolo.empresaId().equals(empresaId) && protocolo.clienteId().equals(clienteId))
                    .toList();
        }

        @Override
        public void salvarSessao(SessaoProtocoloBeautyPro sessao) {
            sessoes.add(sessao);
        }

        @Override
        public List<SessaoProtocoloBeautyPro> listarSessoes(UUID empresaId, UUID protocoloId) {
            return sessoes.stream()
                    .filter(sessao -> sessao.empresaId().equals(empresaId) && sessao.protocoloId().equals(protocoloId))
                    .toList();
        }

        @Override
        public void salvarTermoConsentimento(TermoConsentimentoBeautyPro termo) {
            termos.add(termo);
        }

        @Override
        public List<TermoConsentimentoBeautyPro> listarTermosConsentimento(UUID empresaId, UUID clienteId) {
            return termos.stream()
                    .filter(termo -> termo.empresaId().equals(empresaId) && termo.clienteId().equals(clienteId))
                    .toList();
        }

        @Override
        public void salvarEvidenciaEvolucao(EvidenciaEvolucaoBeautyPro evidencia) {
            evidencias.add(evidencia);
        }

        @Override
        public List<EvidenciaEvolucaoBeautyPro> listarEvidenciasEvolucao(UUID empresaId, UUID clienteId) {
            return evidencias.stream()
                    .filter(evidencia -> evidencia.empresaId().equals(empresaId) && evidencia.clienteId().equals(clienteId))
                    .toList();
        }

        @Override
        public void salvarProdutoUtilizado(ProdutoUtilizadoBeautyPro produto) {
            produtosUtilizados.add(produto);
        }

        @Override
        public List<ProdutoUtilizadoBeautyPro> listarProdutosUtilizados(UUID empresaId, UUID clienteId) {
            return produtosUtilizados.stream()
                    .filter(produto -> produto.empresaId().equals(empresaId) && produto.clienteId().equals(clienteId))
                    .toList();
        }

        @Override
        public List<ProdutoBeautyEstoqueResult> listarProdutosEstoqueBeauty(UUID empresaId, LocalDate hoje) {
            return List.of(new ProdutoBeautyEstoqueResult(
                    UUID.fromString("1a8f23bb-95fd-4640-b2f5-bf45a10a9168"),
                    "Máscara calmante",
                    "Cosmético",
                    "LT-2026",
                    LocalDate.parse("2026-06-10"),
                    "UN",
                    new BigDecimal("3.000"),
                    new BigDecimal("2.000"),
                    false,
                    true
            ));
        }
    }
}
