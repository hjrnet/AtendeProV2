package br.com.atendepro.modules.agenda.application.port.out;

import br.com.atendepro.modules.agenda.domain.model.CompromissoAgenda;

public interface SalvarCompromissoAgendaPort {

    void salvarCompromisso(CompromissoAgenda compromisso);
}
