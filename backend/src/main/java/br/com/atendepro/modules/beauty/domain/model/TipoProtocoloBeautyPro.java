package br.com.atendepro.modules.beauty.domain.model;

public enum TipoProtocoloBeautyPro {
    FACIAL("Facial"),
    CORPORAL("Corporal"),
    CAPILAR("Capilar"),
    CILIOS_SOBRANCELHAS("Cílios e sobrancelhas"),
    SALAO("Salão"),
    PERSONALIZADO("Personalizado");

    private final String rotulo;

    TipoProtocoloBeautyPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static TipoProtocoloBeautyPro deCodigo(String codigo) {
        for (TipoProtocoloBeautyPro tipo : values()) {
            if (tipo.name().equalsIgnoreCase(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de protocolo Beauty invalido: " + codigo);
    }
}
