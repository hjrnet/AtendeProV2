package br.com.atendepro.modules.documento.application.command;

import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;

public record CriarDocumentoProfissionalCommand(
        UUID empresaId,
        UUID clientePacienteId,
        UUID profissionalId,
        String profissionalNome,
        String titulo,
        TipoDocumentoProfissional tipo,
        String conteudo,
        StatusDocumentoProfissional status
) {
}
