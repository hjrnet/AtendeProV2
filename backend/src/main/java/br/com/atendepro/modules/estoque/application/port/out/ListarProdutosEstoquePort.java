package br.com.atendepro.modules.estoque.application.port.out;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.estoque.domain.model.ProdutoEstoque;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarProdutosEstoquePort {

    ResultadoPaginado<ProdutoEstoque> listarProdutosEstoque(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            String categoria,
            Boolean ativo,
            LocalDate vencendoAte
    );
}
