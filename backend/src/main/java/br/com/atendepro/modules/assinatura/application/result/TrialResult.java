package br.com.atendepro.modules.assinatura.application.result;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import br.com.atendepro.modules.assinatura.domain.model.TrialAssinatura;
import br.com.atendepro.modules.assinatura.domain.model.TrialStatus;

public record TrialResult(
        UUID id,
        UUID empresaId,
        UUID planoId,
        TrialStatus status,
        Instant iniciadoEm,
        Instant expiraEm,
        Instant convertidoEm,
        long diasRestantes
) {

    public static TrialResult de(TrialAssinatura trial, Instant agora) {
        TrialStatus status = trial.statusEm(agora);
        long diasRestantes = status == TrialStatus.ATIVO
                ? Math.max(0, ChronoUnit.DAYS.between(agora, trial.expiraEm()))
                : 0;
        return new TrialResult(
                trial.id(),
                trial.empresaId(),
                trial.planoId(),
                status,
                trial.iniciadoEm(),
                trial.expiraEm(),
                trial.convertidoEm(),
                diasRestantes
        );
    }
}
