package br.com.atendepro.modules.mobile.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.mobile.application.result.ClienteVinculadoMobileResult;

public interface ListarClientesVinculadosMobilePort {

    List<ClienteVinculadoMobileResult> listarClientesVinculadosPorEmail(UUID empresaId, String email);
}
