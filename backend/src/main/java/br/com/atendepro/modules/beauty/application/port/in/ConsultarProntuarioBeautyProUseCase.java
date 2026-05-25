package br.com.atendepro.modules.beauty.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.beauty.application.command.ConsultarProntuarioBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.ProntuarioBeautyProResult;

public interface ConsultarProntuarioBeautyProUseCase {
    Optional<ProntuarioBeautyProResult> consultarProntuarioBeautyPro(ConsultarProntuarioBeautyProCommand command);
}
