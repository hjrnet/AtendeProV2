package br.com.atendepro.modules.agenda.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.agenda.domain.model.CompromissoAgenda;

public interface CarregarCompromissoAgendaPorIdPort {

    Optional<CompromissoAgenda> carregarCompromissoPorId(UUID compromissoId);
}
