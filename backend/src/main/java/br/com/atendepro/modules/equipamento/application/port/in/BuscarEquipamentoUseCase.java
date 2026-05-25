package br.com.atendepro.modules.equipamento.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.equipamento.application.result.EquipamentoResult;

public interface BuscarEquipamentoUseCase {

    Optional<EquipamentoResult> buscarEquipamentoPorId(UUID equipamentoId);
}
