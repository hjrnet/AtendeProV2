package br.com.atendepro.modules.servico.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.servico.application.result.ServicoProcedimentoResult;

public interface BuscarServicoProcedimentoUseCase {

    Optional<ServicoProcedimentoResult> buscarServicoProcedimentoPorId(UUID servicoId);
}
