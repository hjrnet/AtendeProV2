package br.com.atendepro.modules.beauty.application.port.in;

import br.com.atendepro.modules.beauty.application.command.CriarFichaEsteticaBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.FichaEsteticaBeautyProResult;

public interface CriarFichaEsteticaBeautyProUseCase {
    FichaEsteticaBeautyProResult criarFichaEstetica(CriarFichaEsteticaBeautyProCommand command);
}
