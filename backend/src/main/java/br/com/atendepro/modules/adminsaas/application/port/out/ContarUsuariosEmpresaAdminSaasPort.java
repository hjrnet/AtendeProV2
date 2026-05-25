package br.com.atendepro.modules.adminsaas.application.port.out;

import java.util.UUID;

public interface ContarUsuariosEmpresaAdminSaasPort {

    long contarUsuariosVinculados(UUID empresaId);
}
