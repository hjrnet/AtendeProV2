package br.com.atendepro.modules.beauty.domain.model;

public enum TipoPlaceholderEvolucaoBeautyPro {
    FACE_NEUTRA("Face neutra"),
    CORPORAL_NEUTRO("Corporal neutro"),
    AREA_TRATADA("Área tratada"),
    TEXTUAL("Registro textual");

    private final String rotulo;

    TipoPlaceholderEvolucaoBeautyPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static TipoPlaceholderEvolucaoBeautyPro deCodigo(String codigo) {
        for (TipoPlaceholderEvolucaoBeautyPro tipo : values()) {
            if (tipo.name().equalsIgnoreCase(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de placeholder Beauty invalido: " + codigo);
    }
}
