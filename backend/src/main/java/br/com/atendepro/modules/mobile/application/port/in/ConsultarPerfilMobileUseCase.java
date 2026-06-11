package br.com.atendepro.modules.mobile.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.mobile.application.result.PerfilMobileResult;

public interface ConsultarPerfilMobileUseCase {

    PerfilMobileResult consultarPerfilMobile(UUID usuarioId);
}
