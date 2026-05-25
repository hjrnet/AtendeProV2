package br.com.atendepro.modules.suporte.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.suporte.domain.model.ChamadoSuporte;

public interface CarregarChamadoSuportePorIdPort {

    Optional<ChamadoSuporte> carregarChamadoPorId(UUID chamadoId);
}
