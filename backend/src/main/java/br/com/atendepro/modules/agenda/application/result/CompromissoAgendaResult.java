package br.com.atendepro.modules.agenda.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.agenda.domain.model.AgendaStatus;
import br.com.atendepro.modules.agenda.domain.model.CompromissoAgenda;
import br.com.atendepro.modules.agenda.domain.model.TipoCompromisso;

public record CompromissoAgendaResult(
        UUID id,
        UUID empresaId,
        UUID clientePacienteId,
        UUID profissionalId,
        String profissionalNome,
        String sala,
        TipoCompromisso tipo,
        AgendaStatus status,
        Instant inicio,
        Instant fim,
        String observacoes,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static CompromissoAgendaResult de(CompromissoAgenda compromisso) {
        return new CompromissoAgendaResult(
                compromisso.id(),
                compromisso.empresaId(),
                compromisso.clientePacienteId(),
                compromisso.profissionalId(),
                compromisso.profissionalNome(),
                compromisso.sala(),
                compromisso.tipo(),
                compromisso.status(),
                compromisso.inicio(),
                compromisso.fim(),
                compromisso.observacoes(),
                compromisso.criadoEm(),
                compromisso.atualizadoEm()
        );
    }
}
