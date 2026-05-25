package br.com.atendepro.modules.assinatura.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.assinatura.application.result.AssinaturaResult;

public interface BuscarAssinaturaUseCase {

    Optional<AssinaturaResult> buscarAssinaturaPorId(UUID assinaturaId);
}
