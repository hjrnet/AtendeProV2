package br.com.atendepro.modules.estoque.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.estoque.application.result.ProdutoEstoqueResult;

public interface BuscarProdutoEstoqueUseCase {

    Optional<ProdutoEstoqueResult> buscarProdutoEstoquePorId(UUID produtoId);
}
