package br.com.atendepro.modules.beauty.application.port.in;

import java.util.List;

import br.com.atendepro.modules.beauty.application.command.ListarClientesBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;

public interface ListarClientesBeautyProUseCase {
    List<ClienteBeautyResumoResult> listarClientesBeautyPro(ListarClientesBeautyProCommand command);
}
