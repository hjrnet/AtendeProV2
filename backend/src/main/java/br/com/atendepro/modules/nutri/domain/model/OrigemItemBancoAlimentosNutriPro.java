package br.com.atendepro.modules.nutri.domain.model;

public enum OrigemItemBancoAlimentosNutriPro {
    PADRAO("Padrao AtendePro"),
    PERSONALIZADO("Personalizado");

    private final String rotulo;

    OrigemItemBancoAlimentosNutriPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static OrigemItemBancoAlimentosNutriPro deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        for (OrigemItemBancoAlimentosNutriPro origem : values()) {
            if (origem.name().equalsIgnoreCase(codigo.trim())) {
                return origem;
            }
        }
        throw new IllegalArgumentException("Origem de item Nutri invalida: " + codigo);
    }
}
