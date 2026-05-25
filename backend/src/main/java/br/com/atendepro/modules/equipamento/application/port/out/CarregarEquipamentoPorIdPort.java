package br.com.atendepro.modules.equipamento.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.equipamento.domain.model.Equipamento;

public interface CarregarEquipamentoPorIdPort {

    Optional<Equipamento> carregarEquipamentoPorId(UUID equipamentoId);
}
