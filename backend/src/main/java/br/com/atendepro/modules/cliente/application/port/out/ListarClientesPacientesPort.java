package br.com.atendepro.modules.cliente.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.cliente.domain.model.ClientePaciente;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarClientesPacientesPort {

    ResultadoPaginado<ClientePaciente> listarClientesPacientes(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            AreaCliente area,
            Boolean ativo
    );
}
