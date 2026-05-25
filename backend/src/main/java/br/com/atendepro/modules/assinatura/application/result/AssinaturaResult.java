package br.com.atendepro.modules.assinatura.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.assinatura.domain.model.AssinaturaSaas;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaStatus;

public record AssinaturaResult(
        UUID id,
        UUID empresaId,
        UUID planoId,
        AssinaturaStatus status,
        Instant iniciadoEm,
        Instant canceladoEm,
        Instant bloqueadoEm
) {

    public static AssinaturaResult de(AssinaturaSaas assinatura) {
        return new AssinaturaResult(
                assinatura.id(),
                assinatura.empresaId(),
                assinatura.planoId(),
                assinatura.status(),
                assinatura.iniciadoEm(),
                assinatura.canceladoEm(),
                assinatura.bloqueadoEm()
        );
    }
}
