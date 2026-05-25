package br.com.atendepro.modules.spaces.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface VerificarConflitoOcupacaoSpacesPort {

    boolean existeConflitoOcupacao(UUID empresaId, UUID recursoId, Instant inicioEm, Instant fimEm);
}
