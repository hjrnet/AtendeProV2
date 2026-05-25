package br.com.atendepro.modules.agenda.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.agenda.application.result.CompromissoAgendaResult;
import br.com.atendepro.modules.agenda.domain.model.AgendaStatus;
import br.com.atendepro.modules.agenda.domain.model.TipoCompromisso;

public record CompromissoAgendaResponse(
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

    public static CompromissoAgendaResponse de(CompromissoAgendaResult result) {
        return new CompromissoAgendaResponse(
                result.id(),
                result.empresaId(),
                result.clientePacienteId(),
                result.profissionalId(),
                result.profissionalNome(),
                result.sala(),
                result.tipo(),
                result.status(),
                result.inicio(),
                result.fim(),
                result.observacoes(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
