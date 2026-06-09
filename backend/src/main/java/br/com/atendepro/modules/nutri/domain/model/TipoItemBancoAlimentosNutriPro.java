package br.com.atendepro.modules.nutri.domain.model;

public enum TipoItemBancoAlimentosNutriPro {
    ALIMENTO("Alimento"),
    SUPLEMENTO("Suplemento");

    private final String rotulo;

    TipoItemBancoAlimentosNutriPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static TipoItemBancoAlimentosNutriPro deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        for (TipoItemBancoAlimentosNutriPro tipo : values()) {
            if (tipo.name().equalsIgnoreCase(codigo.trim())) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de item Nutri invalido: " + codigo);
    }
}
