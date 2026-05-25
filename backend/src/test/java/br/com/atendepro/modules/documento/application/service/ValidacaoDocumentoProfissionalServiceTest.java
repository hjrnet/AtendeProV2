package br.com.atendepro.modules.documento.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;

class ValidacaoDocumentoProfissionalServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final String CODIGO_VALIDACAO = "codigo-publico-task-0604";

    @Test
    void deveValidarDocumentoPublicoComDadosLimitados() {
        ValidacaoDocumentoProfissionalService service = new ValidacaoDocumentoProfissionalService(
                codigo -> Optional.of(documento(StatusDocumentoProfissional.EMITIDO, true, true))
        );

        var result = service.validarDocumento(CODIGO_VALIDACAO);

        assertThat(result.valido()).isTrue();
        assertThat(result.codigoValidacao()).isEqualTo(CODIGO_VALIDACAO);
        assertThat(result.titulo()).isEqualTo("Declaracao publica");
        assertThat(result.tipo()).isEqualTo(TipoDocumentoProfissional.DECLARACAO);
        assertThat(result.profissionalNome()).isEqualTo("Dra. Marina");
        assertThat(result.versao()).isEqualTo(1);
    }

    @Test
    void naoDeveValidarDocumentoCanceladoOuInativo() {
        ValidacaoDocumentoProfissionalService service = new ValidacaoDocumentoProfissionalService(
                codigo -> Optional.of(documento(StatusDocumentoProfissional.CANCELADO, true, true))
        );

        var result = service.validarDocumento(CODIGO_VALIDACAO);

        assertThat(result.valido()).isFalse();
        assertThat(result.titulo()).isNull();
        assertThat(result.profissionalNome()).isNull();
    }

    @Test
    void naoDeveValidarDocumentoComValidacaoPublicaInativa() {
        ValidacaoDocumentoProfissionalService service = new ValidacaoDocumentoProfissionalService(
                codigo -> Optional.of(documento(StatusDocumentoProfissional.EMITIDO, true, false))
        );

        var result = service.validarDocumento(CODIGO_VALIDACAO);

        assertThat(result.valido()).isFalse();
        assertThat(result.mensagem()).contains("indisponivel");
    }

    @Test
    void deveResponderInvalidoParaCodigoInexistente() {
        ValidacaoDocumentoProfissionalService service = new ValidacaoDocumentoProfissionalService(
                codigo -> Optional.empty()
        );

        var result = service.validarDocumento("nao-existe");

        assertThat(result.valido()).isFalse();
        assertThat(result.codigoValidacao()).isEqualTo("nao-existe");
        assertThat(result.tipo()).isNull();
    }

    private DocumentoProfissional documento(
            StatusDocumentoProfissional status,
            boolean ativo,
            boolean validacaoPublicaAtiva
    ) {
        return new DocumentoProfissional(
                UUID.randomUUID(),
                EMPRESA_ID,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Dra. Marina",
                "Declaracao publica",
                TipoDocumentoProfissional.DECLARACAO,
                "Conteudo sigiloso que nao deve sair na validacao publica.",
                status,
                1,
                CODIGO_VALIDACAO,
                validacaoPublicaAtiva,
                ativo,
                Instant.parse("2026-05-25T12:00:00Z"),
                Instant.parse("2026-05-25T12:00:00Z")
        );
    }
}
