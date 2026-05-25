package br.com.atendepro.modules.assinatura.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.assinatura.application.result.AssinaturaResult;

public interface GerenciarStatusAssinaturaUseCase {

    Optional<AssinaturaResult> cancelarAssinatura(UUID assinaturaId);

    Optional<AssinaturaResult> bloquearAssinatura(UUID assinaturaId);

    Optional<AssinaturaResult> desbloquearAssinatura(UUID assinaturaId);
}
