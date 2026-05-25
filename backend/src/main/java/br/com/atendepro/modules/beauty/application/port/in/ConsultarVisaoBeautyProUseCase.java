package br.com.atendepro.modules.beauty.application.port.in;

import br.com.atendepro.modules.beauty.application.command.ConsultarVisaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.VisaoBeautyProResult;

public interface ConsultarVisaoBeautyProUseCase {

    VisaoBeautyProResult consultarVisaoBeautyPro(ConsultarVisaoBeautyProCommand command);
}
