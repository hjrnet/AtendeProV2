package br.com.atendepro.modules.nutri.domain.model;

public enum TipoItemPlanoAlimentarNutriPro {
    ALIMENTO("Alimento"),
    SUPLEMENTO("Suplemento/Formulação");

    private final String rotulo;

    TipoItemPlanoAlimentarNutriPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static TipoItemPlanoAlimentarNutriPro deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("tipo do item do plano alimentar e obrigatorio");
        }
        return TipoItemPlanoAlimentarNutriPro.valueOf(codigo.trim().toUpperCase());
    }
}
