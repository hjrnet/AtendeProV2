package br.com.atendepro.modules.adminsaas.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasDetalheResult;

public interface AtualizarBloqueioEmpresaAdminSaasPort {

    Optional<EmpresaAdminSaasDetalheResult> atualizarBloqueioEmpresa(UUID empresaId, boolean bloqueada);
}
