package br.com.atendepro.modules.beauty.application.port.out;

import br.com.atendepro.modules.beauty.domain.model.ProdutoUtilizadoBeautyPro;

public interface SalvarProdutoUtilizadoBeautyProPort {
    void salvarProdutoUtilizado(ProdutoUtilizadoBeautyPro produto);
}
