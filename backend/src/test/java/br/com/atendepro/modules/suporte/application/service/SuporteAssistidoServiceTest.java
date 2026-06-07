package br.com.atendepro.modules.suporte.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.suporte.domain.model.ChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.OrigemMensagemChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;
import br.com.atendepro.shared.domain.exception.BusinessException;

class SuporteAssistidoServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("40677448-3f0d-4a0e-a641-1950f0d9de62");
    private static final UUID CHAMADO_ID = UUID.fromString("db3bd005-4f02-4597-8a0f-9cd733b9cc53");
    private static final UUID USUARIO_ID = UUID.fromString("5fb97a9f-5133-40de-9106-0b8b09bc02e7");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-07T11:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveGerarTriagemCriticaParaProblemaDeAcesso() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.SUPORTE)));
        var service = service(chamado("Nao consigo acessar", "Sistema fora do ar para login", null), List.of());

        var result = service.gerarRespostaAssistida(CHAMADO_ID);

        assertThat(result.categoriaSugerida()).isEqualTo("acesso");
        assertThat(result.prioridadeSugerida()).isEqualTo(PrioridadeChamadoSuporte.CRITICA);
        assertThat(result.statusSugerido()).isEqualTo(StatusChamadoSuporte.EM_ATENDIMENTO);
        assertThat(result.respostaSugerida()).contains("Recebemos seu chamado");
        assertThat(result.proximasAcoes()).anySatisfy(acao -> assertThat(acao).contains("responsavel tecnico"));
        assertThat(result.geradoEm()).isEqualTo(Instant.parse("2026-06-07T11:00:00Z"));
    }

    @Test
    void deveSugerirCategoriaAssinaturaAPartirDasMensagens() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        var mensagem = MensagemChamadoSuporte.registrar(
                CHAMADO_ID,
                USUARIO_ID,
                "Ana",
                OrigemMensagemChamadoSuporte.CLIENTE,
                "Meu pagamento duplicado aparece no plano.",
                Instant.parse("2026-06-07T10:55:00Z")
        );
        var service = service(chamado("Dúvida", "Preciso de ajuda", null), List.of(mensagem));

        var result = service.gerarRespostaAssistida(CHAMADO_ID);

        assertThat(result.categoriaSugerida()).isEqualTo("assinatura");
        assertThat(result.prioridadeSugerida()).isEqualTo(PrioridadeChamadoSuporte.CRITICA);
        assertThat(result.proximasAcoes()).anySatisfy(acao -> assertThat(acao).contains("assinatura"));
    }

    @Test
    void deveFalharQuandoChamadoNaoExiste() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.SUPORTE)));
        var service = new SuporteAssistidoService(
                id -> Optional.empty(),
                id -> List.of(),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        assertThatThrownBy(() -> service.gerarRespostaAssistida(CHAMADO_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Chamado de suporte nao encontrado.");
    }

    private SuporteAssistidoService service(ChamadoSuporte chamado, List<MensagemChamadoSuporte> mensagens) {
        return new SuporteAssistidoService(
                id -> Optional.of(chamado),
                id -> mensagens,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private ChamadoSuporte chamado(String titulo, String descricao, String categoria) {
        return new ChamadoSuporte(
                CHAMADO_ID,
                EMPRESA_ID,
                USUARIO_ID,
                "Ana",
                "ana@atendepro.local",
                titulo,
                descricao,
                PrioridadeChamadoSuporte.MEDIA,
                StatusChamadoSuporte.ABERTO,
                categoria,
                Instant.parse("2026-06-07T10:00:00Z"),
                Instant.parse("2026-06-07T10:00:00Z")
        );
    }
}
