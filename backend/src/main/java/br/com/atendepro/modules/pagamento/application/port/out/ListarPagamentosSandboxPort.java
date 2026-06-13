package br.com.atendepro.modules.pagamento.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.pagamento.application.result.PagamentoSandboxResumoResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarPagamentosSandboxPort {

    ResultadoPaginado<PagamentoSandboxResumoResult> listarPagamentosSandbox(
            Paginacao paginacao,
            UUID empresaId,
            String status
    );
}
