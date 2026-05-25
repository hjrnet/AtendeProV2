package br.com.atendepro.modules.documento.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class HistoricoDocumentoProfissionalTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final UUID USUARIO_ID = UUID.fromString("ff53e66e-32d0-4188-a75a-0c559e787247");
    private static final Instant AGORA = Instant.parse("2026-05-25T00:00:00Z");

    @Test
    void deveRegistrarHistoricoDeSubstituicao() {
        DocumentoProfissional anterior = documento();
        DocumentoProfissional novo = anterior.substituir(
                "Relatorio revisado",
                "Conteudo revisado.",
                StatusDocumentoProfissional.EMITIDO,
                AGORA.plusSeconds(60)
        );

        HistoricoDocumentoProfissional historico = HistoricoDocumentoProfissional.registrarSubstituicao(
                anterior,
                novo,
                "Correcao solicitada pelo profissional.",
                USUARIO_ID,
                AGORA.plusSeconds(60)
        );

        assertThat(historico.documentoId()).isEqualTo(anterior.id());
        assertThat(historico.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(historico.versaoAnterior()).isEqualTo(1);
        assertThat(historico.versaoNova()).isEqualTo(2);
        assertThat(historico.acao()).isEqualTo(AcaoHistoricoDocumentoProfissional.SUBSTITUICAO);
        assertThat(historico.tituloAnterior()).isEqualTo("Declaracao de acompanhamento");
        assertThat(historico.tituloNovo()).isEqualTo("Relatorio revisado");
        assertThat(historico.usuarioId()).isEqualTo(USUARIO_ID);
    }

    @Test
    void deveRegistrarHistoricoDeCancelamento() {
        DocumentoProfissional anterior = documento();
        DocumentoProfissional cancelado = anterior.cancelar(AGORA.plusSeconds(60));

        HistoricoDocumentoProfissional historico = HistoricoDocumentoProfissional.registrarCancelamento(
                anterior,
                cancelado,
                "Documento emitido em duplicidade.",
                USUARIO_ID,
                AGORA.plusSeconds(60)
        );

        assertThat(historico.acao()).isEqualTo(AcaoHistoricoDocumentoProfissional.CANCELAMENTO);
        assertThat(historico.statusAnterior()).isEqualTo(StatusDocumentoProfissional.RASCUNHO);
        assertThat(historico.statusNovo()).isEqualTo(StatusDocumentoProfissional.CANCELADO);
        assertThat(historico.motivo()).isEqualTo("Documento emitido em duplicidade.");
    }

    private DocumentoProfissional documento() {
        return DocumentoProfissional.criar(
                EMPRESA_ID,
                null,
                UUID.randomUUID(),
                "Dra. Marina",
                "Declaracao de acompanhamento",
                TipoDocumentoProfissional.DECLARACAO,
                "Conteudo clinico orientativo.",
                StatusDocumentoProfissional.RASCUNHO,
                AGORA
        );
    }
}
