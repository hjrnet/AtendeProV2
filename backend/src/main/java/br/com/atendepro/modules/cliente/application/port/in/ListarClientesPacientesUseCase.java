package br.com.atendepro.modules.cliente.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.cliente.application.result.ClientePacienteResult;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarClientesPacientesUseCase {

    ResultadoPaginado<ClientePacienteResult> listarClientesPacientes(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            AreaCliente area,
            Boolean ativo
    );
}
