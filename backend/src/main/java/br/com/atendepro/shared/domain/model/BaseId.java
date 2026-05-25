package br.com.atendepro.shared.domain.model;

import java.util.Objects;
import java.util.UUID;

public record BaseId(UUID valor) {

    public BaseId {
        Objects.requireNonNull(valor, "valor do id e obrigatorio");
    }

    public static BaseId novo() {
        return new BaseId(UUID.randomUUID());
    }

    public static BaseId de(String valor) {
        return new BaseId(UUID.fromString(valor));
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
