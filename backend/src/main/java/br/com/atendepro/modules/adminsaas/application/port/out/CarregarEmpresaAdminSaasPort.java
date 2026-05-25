package br.com.atendepro.modules.adminsaas.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasDetalheResult;

public interface CarregarEmpresaAdminSaasPort {

    Optional<EmpresaAdminSaasDetalheResult> carregarEmpresa(UUID empresaId);
}
