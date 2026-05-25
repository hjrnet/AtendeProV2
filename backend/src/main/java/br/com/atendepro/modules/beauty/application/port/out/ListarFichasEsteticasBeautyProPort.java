package br.com.atendepro.modules.beauty.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.FichaEsteticaBeautyPro;

public interface ListarFichasEsteticasBeautyProPort {
    List<FichaEsteticaBeautyPro> listarFichasEsteticas(UUID empresaId, UUID clienteId);
}
