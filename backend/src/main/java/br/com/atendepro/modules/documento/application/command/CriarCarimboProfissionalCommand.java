package br.com.atendepro.modules.documento.application.command;

import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;

public record CriarCarimboProfissionalCommand(
        UUID empresaId,
        UUID profissionalId,
        String profissionalNome,
        ConselhoProfissional conselho,
        String uf,
        String numeroRegistro,
        String assinaturaTexto,
        String clinicaNome
) {
}
