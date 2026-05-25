package br.com.atendepro.modules.agenda.application.port.in;

import br.com.atendepro.modules.agenda.application.command.AgendarCompromissoCommand;
import br.com.atendepro.modules.agenda.application.result.CompromissoAgendaResult;

public interface AgendarCompromissoUseCase {

    CompromissoAgendaResult agendarCompromisso(AgendarCompromissoCommand command);
}
