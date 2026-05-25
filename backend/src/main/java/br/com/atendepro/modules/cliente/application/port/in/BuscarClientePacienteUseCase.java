package br.com.atendepro.modules.cliente.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.cliente.application.result.ClientePacienteResult;

public interface BuscarClientePacienteUseCase {

    Optional<ClientePacienteResult> buscarClientePacientePorId(UUID clienteId);
}
