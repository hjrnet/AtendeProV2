package br.com.atendepro.modules.suporte.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.suporte.application.result.DetalheChamadoSuporteResult;

public interface DetalharChamadoSuporteUseCase {

    Optional<DetalheChamadoSuporteResult> detalharChamado(UUID chamadoId);
}
