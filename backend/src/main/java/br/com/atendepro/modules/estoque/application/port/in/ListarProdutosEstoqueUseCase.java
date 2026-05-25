package br.com.atendepro.modules.estoque.application.port.in;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.estoque.application.result.ProdutoEstoqueResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarProdutosEstoqueUseCase {

    ResultadoPaginado<ProdutoEstoqueResult> listarProdutosEstoque(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            String categoria,
            Boolean ativo,
            LocalDate vencendoAte
    );
}
