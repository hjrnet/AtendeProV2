package br.com.atendepro.modules.documento.adapter.in.web;

import java.time.Instant;

import br.com.atendepro.modules.documento.application.result.ValidacaoDocumentoProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;

public record ValidacaoDocumentoProfissionalResponse(
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

    public static ValidacaoDocumentoProfissionalResponse de(ValidacaoDocumentoProfissionalResult result) {
        return new ValidacaoDocumentoProfissionalResponse(
                result.codigoValidacao(),
                result.valido(),
                result.titulo(),
                result.tipo(),
                result.status(),
                result.profissionalNome(),
                result.versao(),
                result.emitidoEm(),
                result.mensagem()
        );
    }
}
