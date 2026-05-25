package br.com.atendepro.modules.cliente.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.ClientePaciente;

public interface CarregarClientePacientePorIdPort {

    Optional<ClientePaciente> carregarClientePacientePorId(UUID clienteId);
}
