package br.com.atendepro.modules.custo.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.custo.domain.model.CustoGeral;
import br.com.atendepro.modules.custo.domain.model.TipoCustoGeral;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarCustosGeraisPort {

    ResultadoPaginado<CustoGeral> listarCustosGerais(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoCustoGeral tipo,
            String categoria,
            Boolean ativo
    );
}
