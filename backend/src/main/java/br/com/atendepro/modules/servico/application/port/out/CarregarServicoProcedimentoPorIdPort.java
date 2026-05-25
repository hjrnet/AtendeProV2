package br.com.atendepro.modules.servico.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.servico.domain.model.ServicoProcedimento;

public interface CarregarServicoProcedimentoPorIdPort {

    Optional<ServicoProcedimento> carregarServicoProcedimentoPorId(UUID servicoId);
}
