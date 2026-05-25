package br.com.atendepro.modules.beauty.application.port.in;

import java.util.List;

import br.com.atendepro.modules.beauty.application.command.ListarFichasEsteticasBeautyProCommand;
import br.com.atendepro.modules.beauty.application.result.FichaEsteticaBeautyProResult;

public interface ListarFichasEsteticasBeautyProUseCase {
    List<FichaEsteticaBeautyProResult> listarFichasEsteticas(ListarFichasEsteticasBeautyProCommand command);
}
