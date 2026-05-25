package br.com.atendepro.modules.cliente.application.port.out;

import br.com.atendepro.modules.cliente.domain.model.ClientePaciente;

public interface SalvarClientePacientePort {

    void salvarClientePaciente(ClientePaciente cliente);
}
