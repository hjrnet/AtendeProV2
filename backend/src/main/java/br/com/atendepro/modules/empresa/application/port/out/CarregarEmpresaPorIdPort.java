package br.com.atendepro.modules.empresa.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;

public interface CarregarEmpresaPorIdPort {

    Optional<EmpresaTenant> carregarEmpresaPorId(UUID empresaId);
}
