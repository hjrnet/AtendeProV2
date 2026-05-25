package br.com.atendepro.modules.spaces.domain.model;

import java.util.Arrays;

public enum TipoPacoteSublocacaoSpaces {
    HORA,
    TURNO,
    DIARIA,
    MENSAL,
    FIXO_PERCENTUAL;

    public static TipoPacoteSublocacaoSpaces deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("tipo de pacote de sublocacao e obrigatorio");
        }
        String normalizado = codigo.trim().toUpperCase().replace("-", "_").replace(" ", "_").replace("+", "_");
        return Arrays.stream(values())
                .filter(tipo -> tipo.name().equals(normalizado))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("tipo de pacote de sublocacao invalido"));
    }
}
