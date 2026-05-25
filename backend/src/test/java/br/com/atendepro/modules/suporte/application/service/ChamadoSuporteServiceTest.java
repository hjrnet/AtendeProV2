package br.com.atendepro.modules.suporte.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
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
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.suporte.application.command.AbrirChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.application.command.RegistrarMensagemChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.domain.model.ChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.OrigemMensagemChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

class ChamadoSuporteServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("40677448-3f0d-4a0e-a641-1950f0d9de62");
    private static final UUID OUTRA_EMPRESA_ID = UUID.fromString("2f8199be-5b62-44fe-bf08-f7285719f173");
    private static final UUID USUARIO_ID = UUID.fromString("5fb97a9f-5133-40de-9106-0b8b09bc02e7");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T12:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveAbrirChamadoNaEmpresaDoContextoComMensagemInicial() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        ChamadoSuporteFake fake = new ChamadoSuporteFake();
        ChamadoSuporteService service = service(fake);

        var result = service.abrirChamado(command(null));

        assertThat(result.chamado().empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.chamado().prioridade()).isEqualTo(PrioridadeChamadoSuporte.ALTA);
        assertThat(result.mensagens()).hasSize(1);
        assertThat(result.mensagens().get(0).mensagem()).isEqualTo("Dashboard nao atualiza");
    }

    @Test
    void deveListarChamadosTenantScoped() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.PROFISSIONAL)));
        ChamadoSuporte chamado = ChamadoSuporte.abrir(
                EMPRESA_ID,
                USUARIO_ID,
                "Karol",
                "karol@atendepro.local",
                "Erro no plano",
                "Nao consigo abrir o plano.",
                PrioridadeChamadoSuporte.MEDIA,
                "nutri",
                Instant.now(CLOCK)
        );
        ChamadoSuporteService service = service(new ChamadoSuporteFake(List.of(chamado)));

        var result = service.listarChamados(null, new Paginacao(0, 20), "plano", null, PrioridadeChamadoSuporte.MEDIA);

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("titulo").containsExactly("Erro no plano");
    }

    @Test
    void naoDeveDetalharChamadoDeOutraEmpresa() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        ChamadoSuporte chamado = ChamadoSuporte.abrir(
                OUTRA_EMPRESA_ID,
                USUARIO_ID,
                "Ana",
                "ana@atendepro.local",
                "Chamado de outra empresa",
                "Descricao",
                PrioridadeChamadoSuporte.BAIXA,
                null,
                Instant.now(CLOCK)
        );
        ChamadoSuporteService service = service(new ChamadoSuporteFake(List.of(chamado)));

        assertThatThrownBy(() -> service.detalharChamado(chamado.id()))
                .hasMessage("Usuario nao possui acesso a esta empresa.");
    }

    @Test
    void deveRegistrarMensagemNoChamado() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.RECEPCIONISTA)));
        ChamadoSuporte chamado = ChamadoSuporte.abrir(
                EMPRESA_ID,
                USUARIO_ID,
                "Ana",
                "ana@atendepro.local",
                "Dúvida",
                "Preciso de ajuda",
                PrioridadeChamadoSuporte.MEDIA,
                null,
                Instant.now(CLOCK)
        );
        ChamadoSuporteFake fake = new ChamadoSuporteFake(List.of(chamado));
        ChamadoSuporteService service = service(fake);

        var result = service.registrarMensagem(new RegistrarMensagemChamadoSuporteCommand(
                chamado.id(),
                USUARIO_ID,
                "Ana",
                OrigemMensagemChamadoSuporte.CLIENTE,
                "Inclui mais detalhes."
        ));

        assertThat(result.mensagens()).hasSize(1);
        assertThat(result.mensagens().get(0).mensagem()).isEqualTo("Inclui mais detalhes.");
        assertThat(fake.atualizacoes).hasSize(1);
    }

    @Test
    void naoDeveOperarChamadoSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.CLIENTE)));
        ChamadoSuporteService service = service(new ChamadoSuporteFake());

        assertThatThrownBy(() -> service.listarChamados(null, new Paginacao(0, 20), null, null, null))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private ChamadoSuporteService service(ChamadoSuporteFake fake) {
        return new ChamadoSuporteService(
                fake::salvarChamado,
                fake::atualizarChamado,
                fake::carregarChamadoPorId,
                fake::listarChamados,
                fake::salvarMensagem,
                fake::listarMensagens,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private AbrirChamadoSuporteCommand command(UUID empresaId) {
        return new AbrirChamadoSuporteCommand(
                empresaId,
                USUARIO_ID,
                "Karol Nutricionista Demo",
                "karol.nutri@atendepro.local",
                "Dashboard sem dados",
                "Dashboard nao atualiza",
                PrioridadeChamadoSuporte.ALTA,
                "dashboard"
        );
    }

    private static class ChamadoSuporteFake {
        private final List<ChamadoSuporte> chamados = new ArrayList<>();
        private final List<ChamadoSuporte> atualizacoes = new ArrayList<>();
        private final List<br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte> mensagens = new ArrayList<>();

        private ChamadoSuporteFake() {
        }

        private ChamadoSuporteFake(List<ChamadoSuporte> chamados) {
            this.chamados.addAll(chamados);
        }

        private void salvarChamado(ChamadoSuporte chamado) {
            chamados.add(chamado);
        }

        private void atualizarChamado(ChamadoSuporte chamado) {
            atualizacoes.add(chamado);
        }

        private Optional<ChamadoSuporte> carregarChamadoPorId(UUID chamadoId) {
            return chamados.stream().filter(chamado -> chamado.id().equals(chamadoId)).findFirst();
        }

        private ResultadoPaginado<ChamadoSuporte> listarChamados(
                UUID empresaId,
                Paginacao paginacao,
                String busca,
                br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte status,
                PrioridadeChamadoSuporte prioridade
        ) {
            var itens = chamados.stream()
                    .filter(chamado -> chamado.empresaId().equals(empresaId))
                    .filter(chamado -> prioridade == null || chamado.prioridade() == prioridade)
                    .toList();
            return new ResultadoPaginado<>(itens, itens.size(), paginacao.pagina(), paginacao.tamanho());
        }

        private void salvarMensagem(br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte mensagem) {
            mensagens.add(mensagem);
        }

        private List<br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte> listarMensagens(UUID chamadoId) {
            return mensagens.stream()
                    .filter(mensagem -> mensagem.chamadoId().equals(chamadoId))
                    .toList();
        }
    }
}
