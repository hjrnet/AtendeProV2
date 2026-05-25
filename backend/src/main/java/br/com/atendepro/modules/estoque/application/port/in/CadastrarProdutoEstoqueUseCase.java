package br.com.atendepro.modules.estoque.application.port.in;

import br.com.atendepro.modules.estoque.application.command.CadastrarProdutoEstoqueCommand;
import br.com.atendepro.modules.estoque.application.result.ProdutoEstoqueResult;

public interface CadastrarProdutoEstoqueUseCase {

    ProdutoEstoqueResult cadastrarProdutoEstoque(CadastrarProdutoEstoqueCommand command);
}
