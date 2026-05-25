package br.com.atendepro.modules.custo.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.custo.application.result.CustoGeralResult;

public interface BuscarCustoGeralUseCase {

    Optional<CustoGeralResult> buscarCustoGeralPorId(UUID custoId);
}
