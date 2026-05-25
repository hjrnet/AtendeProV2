package br.com.atendepro.modules.agenda.application.port.in;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.agenda.application.result.CompromissoAgendaResult;
import br.com.atendepro.modules.agenda.domain.model.AgendaStatus;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarAgendaUseCase {

    ResultadoPaginado<CompromissoAgendaResult> listarAgenda(
            UUID empresaId,
            Paginacao paginacao,
            Instant inicio,
            Instant fim,
            UUID profissionalId,
            String sala,
            AgendaStatus status
    );
}
