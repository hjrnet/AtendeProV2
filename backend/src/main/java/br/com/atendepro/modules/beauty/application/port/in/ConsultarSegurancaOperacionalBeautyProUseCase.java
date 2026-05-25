package br.com.atendepro.modules.beauty.application.port.in;

import br.com.atendepro.modules.beauty.application.command.ConsultarSegurancaOperacionalBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.SegurancaOperacionalBeautyProResult;

public interface ConsultarSegurancaOperacionalBeautyProUseCase {
    SegurancaOperacionalBeautyProResult consultarSegurancaOperacional(ConsultarSegurancaOperacionalBeautyProCommand command);
}
