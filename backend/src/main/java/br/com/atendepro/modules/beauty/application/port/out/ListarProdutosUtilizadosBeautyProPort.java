package br.com.atendepro.modules.beauty.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.ProdutoUtilizadoBeautyPro;

public interface ListarProdutosUtilizadosBeautyProPort {
    List<ProdutoUtilizadoBeautyPro> listarProdutosUtilizados(UUID empresaId, UUID clienteId);
}
