package br.com.atendepro.modules.empresa.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.empresa.application.result.EmpresaResult;

public interface BuscarEmpresaUseCase {

    Optional<EmpresaResult> buscarEmpresaPorId(UUID empresaId);
}
