package br.com.atendepro.modules.custo.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.custo.domain.model.CustoAlimentacaoTransporte;
import br.com.atendepro.modules.custo.domain.model.TipoCustoPessoal;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarCustosAlimentacaoTransportePort {

    ResultadoPaginado<CustoAlimentacaoTransporte> listarCustosAlimentacaoTransporte(
            UUID empresaId,
            Paginacao paginacao,
            TipoCustoPessoal tipo,
            UUID profissionalId,
            Boolean ativo
    );
}
