package br.com.atendepro.modules.estoque.application.port.out;

import br.com.atendepro.modules.estoque.domain.model.ProdutoEstoque;

public interface SalvarProdutoEstoquePort {

    void salvarProdutoEstoque(ProdutoEstoque produto);
}
