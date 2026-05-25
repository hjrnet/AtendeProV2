package br.com.atendepro.modules.agenda.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.agenda.application.result.CompromissoAgendaResult;

public interface BuscarCompromissoAgendaUseCase {

    Optional<CompromissoAgendaResult> buscarCompromissoPorId(UUID compromissoId);
}
