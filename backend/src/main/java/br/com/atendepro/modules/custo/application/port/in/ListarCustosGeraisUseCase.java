package br.com.atendepro.modules.custo.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.custo.application.result.CustoGeralResult;
import br.com.atendepro.modules.custo.domain.model.TipoCustoGeral;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarCustosGeraisUseCase {

    ResultadoPaginado<CustoGeralResult> listarCustosGerais(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoCustoGeral tipo,
            String categoria,
            Boolean ativo
    );
}
