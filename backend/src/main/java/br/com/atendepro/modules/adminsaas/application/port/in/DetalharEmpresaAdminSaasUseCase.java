package br.com.atendepro.modules.adminsaas.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasDetalheResult;

public interface DetalharEmpresaAdminSaasUseCase {

    Optional<EmpresaAdminSaasDetalheResult> detalharEmpresa(UUID empresaId);
}
