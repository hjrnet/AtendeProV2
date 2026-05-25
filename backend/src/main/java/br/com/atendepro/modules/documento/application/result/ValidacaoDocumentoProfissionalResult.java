package br.com.atendepro.modules.documento.application.result;

import java.time.Instant;

import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;

public record ValidacaoDocumentoProfissionalResult(
        String codigoValidacao,
        boolean valido,
        String titulo,
        TipoDocumentoProfissional tipo,
        StatusDocumentoProfissional status,
        String profissionalNome,
        int versao,
        Instant emitidoEm,
        String mensagem
) {

    public static ValidacaoDocumentoProfissionalResult valida(DocumentoProfissional documento) {
        return new ValidacaoDocumentoProfissionalResult(
                documento.codigoValidacao(),
                true,
                documento.titulo(),
                documento.tipo(),
                documento.status(),
                documento.profissionalNome(),
                documento.versao(),
                documento.atualizadoEm(),
                "Documento profissional encontrado."
        );
    }

    public static ValidacaoDocumentoProfissionalResult invalida(String codigoValidacao) {
        return new ValidacaoDocumentoProfissionalResult(
                codigoValidacao,
                false,
                null,
                null,
                null,
                null,
                0,
                null,
                "Documento profissional nao encontrado ou indisponivel para validacao publica."
        );
    }
}
