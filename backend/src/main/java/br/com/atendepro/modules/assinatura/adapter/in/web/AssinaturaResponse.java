package br.com.atendepro.modules.assinatura.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.assinatura.application.result.AssinaturaResult;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaStatus;

public record AssinaturaResponse(
        UUID id,
        UUID empresaId,
        UUID planoId,
        AssinaturaStatus status,
        Instant iniciadoEm,
        Instant canceladoEm,
        Instant bloqueadoEm
) {

    static AssinaturaResponse de(AssinaturaResult result) {
        return new AssinaturaResponse(
                result.id(),
                result.empresaId(),
                result.planoId(),
                result.status(),
                result.iniciadoEm(),
                result.canceladoEm(),
                result.bloqueadoEm()
        );
    }
}
