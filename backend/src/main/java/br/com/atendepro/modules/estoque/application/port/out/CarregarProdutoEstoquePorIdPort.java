package br.com.atendepro.modules.estoque.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.estoque.domain.model.ProdutoEstoque;

public interface CarregarProdutoEstoquePorIdPort {

    Optional<ProdutoEstoque> carregarProdutoEstoquePorId(UUID produtoId);
}
