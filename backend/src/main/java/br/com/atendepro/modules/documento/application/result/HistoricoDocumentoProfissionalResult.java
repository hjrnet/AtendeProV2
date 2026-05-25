package br.com.atendepro.modules.documento.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.AcaoHistoricoDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.HistoricoDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;

public record HistoricoDocumentoProfissionalResult(
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

    public static HistoricoDocumentoProfissionalResult de(HistoricoDocumentoProfissional historico) {
        return new HistoricoDocumentoProfissionalResult(
                historico.id(),
                historico.documentoId(),
                historico.empresaId(),
                historico.versaoAnterior(),
                historico.versaoNova(),
                historico.acao(),
                historico.tituloAnterior(),
                historico.conteudoAnterior(),
                historico.statusAnterior(),
                historico.tituloNovo(),
                historico.conteudoNovo(),
                historico.statusNovo(),
                historico.motivo(),
                historico.usuarioId(),
                historico.criadoEm()
        );
    }
}
