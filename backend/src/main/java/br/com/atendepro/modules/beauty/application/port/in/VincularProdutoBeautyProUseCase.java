package br.com.atendepro.modules.beauty.application.port.in;

import br.com.atendepro.modules.beauty.application.command.VincularProdutoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.ProdutoUtilizadoBeautyProResult;

public interface VincularProdutoBeautyProUseCase {
    ProdutoUtilizadoBeautyProResult vincularProduto(VincularProdutoBeautyProCommand command);
}
