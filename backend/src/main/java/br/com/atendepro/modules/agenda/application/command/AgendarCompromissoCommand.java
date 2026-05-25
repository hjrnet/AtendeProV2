package br.com.atendepro.modules.agenda.application.command;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.agenda.domain.model.TipoCompromisso;

public record AgendarCompromissoCommand(
        UUID empresaId,
        UUID clientePacienteId,
        UUID profissionalId,
        String profissionalNome,
        String sala,
        TipoCompromisso tipo,
        Instant inicio,
        Instant fim,
        String observacoes
) {
}
