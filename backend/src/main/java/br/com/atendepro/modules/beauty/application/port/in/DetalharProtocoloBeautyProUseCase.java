package br.com.atendepro.modules.beauty.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.beauty.application.command.DetalharProtocoloBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.ProtocoloBeautyProResult;

public interface DetalharProtocoloBeautyProUseCase {
    Optional<ProtocoloBeautyProResult> detalharProtocolo(DetalharProtocoloBeautyProCommand command);
}
