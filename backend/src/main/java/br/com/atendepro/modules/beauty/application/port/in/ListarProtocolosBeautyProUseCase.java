package br.com.atendepro.modules.beauty.application.port.in;

import java.util.List;

import br.com.atendepro.modules.beauty.application.command.ListarProtocolosBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.ProtocoloBeautyProResult;

public interface ListarProtocolosBeautyProUseCase {
    List<ProtocoloBeautyProResult> listarProtocolos(ListarProtocolosBeautyProCommand command);
}
