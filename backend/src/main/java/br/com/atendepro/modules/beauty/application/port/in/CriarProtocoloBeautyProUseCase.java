package br.com.atendepro.modules.beauty.application.port.in;

import br.com.atendepro.modules.beauty.application.command.CriarProtocoloBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.ProtocoloBeautyProResult;

public interface CriarProtocoloBeautyProUseCase {
    ProtocoloBeautyProResult criarProtocolo(CriarProtocoloBeautyProCommand command);
}
