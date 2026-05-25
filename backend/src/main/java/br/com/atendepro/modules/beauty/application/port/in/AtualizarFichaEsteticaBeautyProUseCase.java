package br.com.atendepro.modules.beauty.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.beauty.application.command.AtualizarFichaEsteticaBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.FichaEsteticaBeautyProResult;

public interface AtualizarFichaEsteticaBeautyProUseCase {
    Optional<FichaEsteticaBeautyProResult> atualizarFichaEstetica(AtualizarFichaEsteticaBeautyProCommand command);
}
