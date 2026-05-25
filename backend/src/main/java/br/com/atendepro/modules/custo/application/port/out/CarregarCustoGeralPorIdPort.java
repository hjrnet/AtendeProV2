package br.com.atendepro.modules.custo.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.custo.domain.model.CustoGeral;

public interface CarregarCustoGeralPorIdPort {

    Optional<CustoGeral> carregarCustoGeralPorId(UUID custoId);
}
