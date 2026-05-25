package br.com.atendepro.modules.documento.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;

public record DocumentoProfissionalResponse(
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

    public static DocumentoProfissionalResponse de(DocumentoProfissionalResult result) {
        return new DocumentoProfissionalResponse(
                result.id(),
                result.empresaId(),
                result.clientePacienteId(),
                result.profissionalId(),
                result.profissionalNome(),
                result.titulo(),
                result.tipo(),
                result.conteudo(),
                result.status(),
                result.versao(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
