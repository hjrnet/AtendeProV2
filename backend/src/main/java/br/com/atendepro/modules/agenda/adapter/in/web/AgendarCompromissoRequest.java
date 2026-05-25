package br.com.atendepro.modules.agenda.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.agenda.application.command.AgendarCompromissoCommand;
import br.com.atendepro.modules.agenda.domain.model.TipoCompromisso;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgendarCompromissoRequest(
        UUID empresaId,
        UUID clientePacienteId,
        UUID profissionalId,
        @Size(max = 160) String profissionalNome,
        @Size(max = 120) String sala,
        @NotNull TipoCompromisso tipo,
        @NotNull Instant inicio,
        @NotNull Instant fim,
        @Size(max = 1000) String observacoes
) {

    public AgendarCompromissoCommand paraCommand() {
        return new AgendarCompromissoCommand(
                empresaId,
                clientePacienteId,
                profissionalId,
                profissionalNome,
                sala,
                tipo,
                inicio,
                fim,
                observacoes
        );
    }
}
