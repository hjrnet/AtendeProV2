package br.com.atendepro.modules.agenda.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface VerificarConflitoAgendaPort {

    boolean existeConflitoAgenda(
            UUID empresaId,
            UUID profissionalId,
            String sala,
            Instant inicio,
            Instant fim
    );
}
