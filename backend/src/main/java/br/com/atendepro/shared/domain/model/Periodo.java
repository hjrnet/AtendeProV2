package br.com.atendepro.shared.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record Periodo(LocalDate inicio, LocalDate fim) {

    public Periodo {
        Objects.requireNonNull(inicio, "data inicial e obrigatoria");
        Objects.requireNonNull(fim, "data final e obrigatoria");
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("data final nao pode ser anterior a data inicial");
        }
    }

    public boolean contem(LocalDate data) {
        Objects.requireNonNull(data, "data e obrigatoria");
        return !data.isBefore(inicio) && !data.isAfter(fim);
    }

    public long totalDiasInclusivo() {
        return ChronoUnit.DAYS.between(inicio, fim) + 1;
    }
}
