package br.com.atendepro.modules.beauty.domain.model;

public enum StatusTermoBeautyPro {
    GERADO("Gerado"),
    ACEITO("Aceito"),
    CANCELADO("Cancelado");

    private final String rotulo;

    StatusTermoBeautyPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static StatusTermoBeautyPro deCodigo(String codigo) {
        for (StatusTermoBeautyPro status : values()) {
            if (status.name().equalsIgnoreCase(codigo)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de termo Beauty invalido: " + codigo);
    }
}
