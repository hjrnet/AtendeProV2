package br.com.atendepro.modules.beauty.domain.model;

public enum ObjetivoEsteticoBeautyPro {
    ACNE("Acne"),
    MANCHAS("Manchas"),
    REJUVENESCIMENTO("Rejuvenescimento"),
    CORPORAL("Corporal"),
    RELAXAMENTO("Relaxamento"),
    CAPILAR("Capilar"),
    CILIOS_SOBRANCELHAS("Cílios e sobrancelhas"),
    SALAO("Salão");

    private final String rotulo;

    ObjetivoEsteticoBeautyPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static ObjetivoEsteticoBeautyPro deCodigo(String codigo) {
        for (ObjetivoEsteticoBeautyPro objetivo : values()) {
            if (objetivo.name().equalsIgnoreCase(codigo)) {
                return objetivo;
            }
        }
        throw new IllegalArgumentException("Objetivo estetico invalido: " + codigo);
    }
}
