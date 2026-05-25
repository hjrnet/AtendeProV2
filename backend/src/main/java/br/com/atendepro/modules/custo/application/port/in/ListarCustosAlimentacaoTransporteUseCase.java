package br.com.atendepro.modules.custo.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.custo.application.result.CustoAlimentacaoTransporteResult;
import br.com.atendepro.modules.custo.domain.model.TipoCustoPessoal;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarCustosAlimentacaoTransporteUseCase {

    ResultadoPaginado<CustoAlimentacaoTransporteResult> listarCustosAlimentacaoTransporte(
            UUID empresaId,
            Paginacao paginacao,
            TipoCustoPessoal tipo,
            UUID profissionalId,
            Boolean ativo
    );
}
