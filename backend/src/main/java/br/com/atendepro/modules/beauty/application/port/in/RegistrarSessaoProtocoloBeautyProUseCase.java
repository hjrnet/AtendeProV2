package br.com.atendepro.modules.beauty.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.beauty.application.command.RegistrarSessaoProtocoloBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.SessaoProtocoloBeautyProResult;

public interface RegistrarSessaoProtocoloBeautyProUseCase {
    Optional<SessaoProtocoloBeautyProResult> registrarSessao(RegistrarSessaoProtocoloBeautyProCommand command);
}
