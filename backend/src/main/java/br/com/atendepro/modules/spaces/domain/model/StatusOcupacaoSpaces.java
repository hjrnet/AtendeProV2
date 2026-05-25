package br.com.atendepro.modules.spaces.domain.model;

public enum StatusOcupacaoSpaces {
    RESERVADA,
    CONFIRMADA,
    CANCELADA;

    public static StatusOcupacaoSpaces deCodigo(String codigo) {
        for (StatusOcupacaoSpaces status : values()) {
            if (status.name().equalsIgnoreCase(codigo)) {
                return status;
            }
        }
        throw new IllegalArgumentException("status de ocupacao spaces invalido: " + codigo);
    }

    public boolean bloqueiaDisponibilidade() {
        return this == RESERVADA || this == CONFIRMADA;
    }
}
