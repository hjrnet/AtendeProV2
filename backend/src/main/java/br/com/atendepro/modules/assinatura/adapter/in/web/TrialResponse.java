package br.com.atendepro.modules.assinatura.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.assinatura.application.result.TrialResult;
import br.com.atendepro.modules.assinatura.domain.model.TrialStatus;

public record TrialResponse(
        UUID id,
        UUID empresaId,
        UUID planoId,
        TrialStatus status,
        Instant iniciadoEm,
        Instant expiraEm,
        Instant convertidoEm,
        long diasRestantes
) {

    static TrialResponse de(TrialResult result) {
        return new TrialResponse(
                result.id(),
                result.empresaId(),
                result.planoId(),
                result.status(),
                result.iniciadoEm(),
                result.expiraEm(),
                result.convertidoEm(),
                result.diasRestantes()
        );
    }
}
