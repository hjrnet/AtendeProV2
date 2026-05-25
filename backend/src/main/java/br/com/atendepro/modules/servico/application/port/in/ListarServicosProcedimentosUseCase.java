package br.com.atendepro.modules.servico.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.servico.application.result.ServicoProcedimentoResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarServicosProcedimentosUseCase {

    ResultadoPaginado<ServicoProcedimentoResult> listarServicosProcedimentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            AreaCliente area,
            Boolean ativo
    );
}
