package br.com.atendepro.modules.servico.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.servico.domain.model.ServicoProcedimento;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarServicosProcedimentosPort {

    ResultadoPaginado<ServicoProcedimento> listarServicosProcedimentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            AreaCliente area,
            Boolean ativo
    );
}
