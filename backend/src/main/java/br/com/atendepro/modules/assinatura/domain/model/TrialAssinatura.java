package br.com.atendepro.modules.assinatura.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public record TrialAssinatura(
        UUID id,
        UUID empresaId,
        UUID planoId,
        TrialStatus status,
        Instant iniciadoEm,
        Instant expiraEm,
        Instant convertidoEm,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static final int DIAS_TRIAL = 30;

    public TrialAssinatura {
        if (id == null) {
            throw new IllegalArgumentException("id do trial e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do trial e obrigatoria");
        }
        if (planoId == null) {
            throw new IllegalArgumentException("plano do trial e obrigatorio");
        }
        if (status == null) {
            throw new IllegalArgumentException("status do trial e obrigatorio");
        }
        if (iniciadoEm == null || expiraEm == null || criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do trial sao obrigatorias");
        }
        if (expiraEm.isBefore(iniciadoEm) || expiraEm.equals(iniciadoEm)) {
            throw new IllegalArgumentException("trial deve expirar apos o inicio");
        }
        if (status == TrialStatus.CONVERTIDO && convertidoEm == null) {
            throw new IllegalArgumentException("trial convertido exige data de conversao");
        }
    }

    public static TrialAssinatura iniciar(UUID empresaId, UUID planoId, Instant agora) {
        return new TrialAssinatura(
                UUID.randomUUID(),
                empresaId,
                planoId,
                TrialStatus.ATIVO,
                agora,
                agora.plus(DIAS_TRIAL, ChronoUnit.DAYS),
                null,
                agora,
                agora
        );
    }

    public TrialStatus statusEm(Instant agora) {
        if (status == TrialStatus.ATIVO && !agora.isBefore(expiraEm)) {
            return TrialStatus.EXPIRADO;
        }
        return status;
    }

    public TrialAssinatura converter(Instant agora) {
        if (statusEm(agora) != TrialStatus.ATIVO) {
            throw new IllegalArgumentException("somente trial ativo pode ser convertido");
        }
        return new TrialAssinatura(
                id,
                empresaId,
                planoId,
                TrialStatus.CONVERTIDO,
                iniciadoEm,
                expiraEm,
                agora,
                criadoEm,
                agora
        );
    }
}
