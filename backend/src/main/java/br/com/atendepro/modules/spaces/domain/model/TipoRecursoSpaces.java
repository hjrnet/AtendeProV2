package br.com.atendepro.modules.spaces.domain.model;

import java.util.Arrays;

public enum TipoRecursoSpaces {
    SALA,
    CADEIRA,
    CABINE,
    EQUIPAMENTO;

    public static TipoRecursoSpaces deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("tipo de recurso do spaces e obrigatorio");
        }
        String normalizado = codigo.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        return Arrays.stream(values())
                .filter(tipo -> tipo.name().equals(normalizado))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("tipo de recurso do spaces invalido"));
    }
}
