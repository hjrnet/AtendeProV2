package br.com.atendepro.modules.documento.application.service;

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
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.documento.application.command.CancelarDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.application.command.SubstituirDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.application.port.out.AtualizarDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.application.port.out.RegistrarHistoricoDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.domain.model.AcaoHistoricoDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.HistoricoDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class DocumentoProfissionalHistoricoServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final UUID OUTRA_EMPRESA_ID = UUID.fromString("f899c2f6-2bc1-4e08-b730-686c42e07850");
    private static final UUID USUARIO_ID = UUID.fromString("ff53e66e-32d0-4188-a75a-0c559e787247");
    private static final UUID DOCUMENTO_ID = UUID.fromString("75c580d4-558a-4c2a-ad46-04a8082d8943");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveSubstituirDocumentoRegistrandoHistorico() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.PROFISSIONAL)));
        AtualizarDocumentoFake atualizarFake = new AtualizarDocumentoFake();
        RegistrarHistoricoFake historicoFake = new RegistrarHistoricoFake();
        DocumentoProfissionalHistoricoService service = service(
                documento(StatusDocumentoProfissional.RASCUNHO, true, true, EMPRESA_ID),
                atualizarFake,
                historicoFake
        );

        var result = service.substituirDocumento(new SubstituirDocumentoProfissionalCommand(
                DOCUMENTO_ID,
                "Declaracao revisada",
                "Conteudo revisado.",
                StatusDocumentoProfissional.EMITIDO,
                "Ajuste de informacoes antes da entrega."
        ));

        assertThat(result.versao()).isEqualTo(2);
        assertThat(result.status()).isEqualTo(StatusDocumentoProfissional.EMITIDO);
        assertThat(atualizarFake.documentoAtualizado.titulo()).isEqualTo("Declaracao revisada");
        assertThat(historicoFake.historicoRegistrado.acao()).isEqualTo(AcaoHistoricoDocumentoProfissional.SUBSTITUICAO);
        assertThat(historicoFake.historicoRegistrado.usuarioId()).isEqualTo(USUARIO_ID);
    }

    @Test
    void deveCancelarDocumentoRegistrandoHistoricoEDesativandoValidacao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        AtualizarDocumentoFake atualizarFake = new AtualizarDocumentoFake();
        RegistrarHistoricoFake historicoFake = new RegistrarHistoricoFake();
        DocumentoProfissionalHistoricoService service = service(
                documento(StatusDocumentoProfissional.EMITIDO, true, true, EMPRESA_ID),
                atualizarFake,
                historicoFake
        );

        var result = service.cancelarDocumento(new CancelarDocumentoProfissionalCommand(
                DOCUMENTO_ID,
                "Documento emitido em duplicidade."
        ));

        assertThat(result.status()).isEqualTo(StatusDocumentoProfissional.CANCELADO);
        assertThat(result.versao()).isEqualTo(2);
        assertThat(result.ativo()).isFalse();
        assertThat(result.validacaoPublicaAtiva()).isFalse();
        assertThat(historicoFake.historicoRegistrado.acao()).isEqualTo(AcaoHistoricoDocumentoProfissional.CANCELAMENTO);
    }

    @Test
    void deveListarHistoricoDoDocumentoDaEmpresa() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.PROFISSIONAL)));
        DocumentoProfissional documento = documento(StatusDocumentoProfissional.EMITIDO, true, true, EMPRESA_ID);
        HistoricoDocumentoProfissional historico = HistoricoDocumentoProfissional.registrarSubstituicao(
                documento,
                documento.substituir("Novo titulo", "Novo conteudo.", StatusDocumentoProfissional.EMITIDO, Instant.now(CLOCK)),
                "Revisao.",
                USUARIO_ID,
                Instant.now(CLOCK)
        );
        DocumentoProfissionalHistoricoService service = new DocumentoProfissionalHistoricoService(
                id -> Optional.of(documento),
                documentoAtualizado -> {
                },
                historicoRegistrado -> {
                },
                (documentoId, empresaId, paginacao) -> {
                    assertThat(documentoId).isEqualTo(DOCUMENTO_ID);
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    return new ResultadoPaginado<>(List.of(historico), 1, paginacao.pagina(), paginacao.tamanho());
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarHistorico(DOCUMENTO_ID, new Paginacao(0, 20));

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("acao").containsExactly(AcaoHistoricoDocumentoProfissional.SUBSTITUICAO);
    }

    @Test
    void naoDeveAlterarDocumentoCancelado() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        DocumentoProfissionalHistoricoService service = service(
                documento(StatusDocumentoProfissional.CANCELADO, false, false, EMPRESA_ID),
                documentoAtualizado -> {
                },
                historicoRegistrado -> {
                }
        );

        assertThatThrownBy(() -> service.cancelarDocumento(new CancelarDocumentoProfissionalCommand(
                DOCUMENTO_ID,
                "Tentativa duplicada."
        )))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Documento profissional cancelado nao pode ser alterado.");
    }

    @Test
    void naoDeveAlterarDocumentoDeOutraEmpresa() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.PROFISSIONAL)));
        DocumentoProfissionalHistoricoService service = service(
                documento(StatusDocumentoProfissional.EMITIDO, true, true, OUTRA_EMPRESA_ID),
                documentoAtualizado -> {
                },
                historicoRegistrado -> {
                }
        );

        assertThatThrownBy(() -> service.substituirDocumento(new SubstituirDocumentoProfissionalCommand(
                DOCUMENTO_ID,
                "Novo titulo",
                "Novo conteudo.",
                StatusDocumentoProfissional.EMITIDO,
                "Revisao."
        )))
                .hasMessage("Usuario nao possui acesso a esta empresa.");
    }

    @Test
    void naoDeveOperarSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, USUARIO_ID, Set.of(PerfilAcesso.CLIENTE)));
        DocumentoProfissionalHistoricoService service = service(
                documento(StatusDocumentoProfissional.EMITIDO, true, true, EMPRESA_ID),
                documentoAtualizado -> {
                },
                historicoRegistrado -> {
                }
        );

        assertThatThrownBy(() -> service.listarHistorico(DOCUMENTO_ID, new Paginacao(0, 20)))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private DocumentoProfissionalHistoricoService service(
            DocumentoProfissional documento,
            AtualizarDocumentoProfissionalPort atualizarPort,
            RegistrarHistoricoDocumentoProfissionalPort historicoPort
    ) {
        return new DocumentoProfissionalHistoricoService(
                id -> Optional.of(documento),
                atualizarPort,
                historicoPort,
                (documentoId, empresaId, paginacao) ->
                        new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private DocumentoProfissional documento(
            StatusDocumentoProfissional status,
            boolean ativo,
            boolean validacaoPublicaAtiva,
            UUID empresaId
    ) {
        return new DocumentoProfissional(
                DOCUMENTO_ID,
                empresaId,
                null,
                UUID.randomUUID(),
                "Dra. Marina",
                "Declaracao original",
                TipoDocumentoProfissional.DECLARACAO,
                "Conteudo original.",
                status,
                1,
                "codigo-validacao-task-0607",
                validacaoPublicaAtiva,
                ativo,
                Instant.parse("2026-05-25T00:00:00Z"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class AtualizarDocumentoFake implements AtualizarDocumentoProfissionalPort {

        private DocumentoProfissional documentoAtualizado;

        @Override
        public void atualizarDocumento(DocumentoProfissional documento) {
            this.documentoAtualizado = documento;
        }
    }

    private static class RegistrarHistoricoFake implements RegistrarHistoricoDocumentoProfissionalPort {

        private HistoricoDocumentoProfissional historicoRegistrado;

        @Override
        public void registrarHistorico(HistoricoDocumentoProfissional historico) {
            this.historicoRegistrado = historico;
        }
    }
}
