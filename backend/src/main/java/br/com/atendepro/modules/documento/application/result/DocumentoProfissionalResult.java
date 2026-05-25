package br.com.atendepro.modules.documento.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;

public record DocumentoProfissionalResult(
        UUID id,
        UUID empresaId,
        UUID clientePacienteId,
        UUID profissionalId,
        String profissionalNome,
        String titulo,
        TipoDocumentoProfissional tipo,
        String conteudo,
        StatusDocumentoProfissional status,
        int versao,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static DocumentoProfissionalResult de(DocumentoProfissional documento) {
        return new DocumentoProfissionalResult(
                documento.id(),
                documento.empresaId(),
                documento.clientePacienteId(),
                documento.profissionalId(),
                documento.profissionalNome(),
                documento.titulo(),
                documento.tipo(),
                documento.conteudo(),
                documento.status(),
                documento.versao(),
                documento.ativo(),
                documento.criadoEm(),
                documento.atualizadoEm()
        );
    }
}
