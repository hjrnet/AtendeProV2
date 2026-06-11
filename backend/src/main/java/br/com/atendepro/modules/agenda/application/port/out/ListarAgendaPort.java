package br.com.atendepro.modules.agenda.application.port.out;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.agenda.domain.model.AgendaStatus;
import br.com.atendepro.modules.agenda.domain.model.CompromissoAgenda;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarAgendaPort {

    ResultadoPaginado<CompromissoAgenda> listarAgenda(
            UUID empresaId,
            Paginacao paginacao,
            UUID clientePacienteId,
            Instant inicio,
            Instant fim,
            UUID profissionalId,
            String sala,
            AgendaStatus status
    );
}
