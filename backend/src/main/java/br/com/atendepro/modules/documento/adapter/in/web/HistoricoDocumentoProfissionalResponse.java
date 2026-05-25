package br.com.atendepro.modules.documento.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.HistoricoDocumentoProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.AcaoHistoricoDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;

public record HistoricoDocumentoProfissionalResponse(
        UUID id,
        UUID documentoId,
        UUID empresaId,
        int versaoAnterior,
        int versaoNova,
        AcaoHistoricoDocumentoProfissional acao,
        String tituloAnterior,
        String conteudoAnterior,
        StatusDocumentoProfissional statusAnterior,
        String tituloNovo,
        String conteudoNovo,
        StatusDocumentoProfissional statusNovo,
        String motivo,
        UUID usuarioId,
        Instant criadoEm
) {

    public static HistoricoDocumentoProfissionalResponse de(HistoricoDocumentoProfissionalResult result) {
        return new HistoricoDocumentoProfissionalResponse(
                result.id(),
                result.documentoId(),
                result.empresaId(),
                result.versaoAnterior(),
                result.versaoNova(),
                result.acao(),
                result.tituloAnterior(),
                result.conteudoAnterior(),
                result.statusAnterior(),
                result.tituloNovo(),
                result.conteudoNovo(),
                result.statusNovo(),
                result.motivo(),
                result.usuarioId(),
                result.criadoEm()
        );
    }
}
