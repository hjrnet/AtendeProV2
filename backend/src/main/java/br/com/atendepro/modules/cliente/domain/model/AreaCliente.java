package br.com.atendepro.modules.cliente.domain.model;

import java.util.Arrays;

public enum AreaCliente {
    GERAL,
    NUTRI,
    BEAUTY,
    BIOMED,
    FISIO,
    SPACES,
    PSICO,
    FONO,
    FARMACIA_CLINICA,
    ODONTO,
    TERAPIAS_INTEGRATIVAS;

    public static AreaCliente deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return GERAL;
        }
        String normalizado = codigo.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        return Arrays.stream(values())
                .filter(area -> area.name().equals(normalizado))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("area de cliente invalida"));
    }
}
