package br.com.atendepro.modules.beauty.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.FichaEsteticaBeautyPro;

public interface CarregarFichaEsteticaBeautyProPort {
    Optional<FichaEsteticaBeautyPro> carregarFichaAtual(UUID empresaId, UUID clienteId);

    Optional<FichaEsteticaBeautyPro> carregarFichaEstetica(UUID empresaId, UUID clienteId, UUID fichaId);
}
