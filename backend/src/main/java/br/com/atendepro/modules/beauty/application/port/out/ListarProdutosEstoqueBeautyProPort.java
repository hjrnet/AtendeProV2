package br.com.atendepro.modules.beauty.application.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.ProdutoBeautyEstoqueResult;

public interface ListarProdutosEstoqueBeautyProPort {
    List<ProdutoBeautyEstoqueResult> listarProdutosEstoqueBeauty(UUID empresaId, LocalDate hoje);
}
