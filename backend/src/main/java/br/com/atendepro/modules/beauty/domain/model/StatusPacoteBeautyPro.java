package br.com.atendepro.modules.beauty.domain.model;

public enum StatusPacoteBeautyPro {
    ATIVO("Ativo"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado"),
    PAUSADO("Pausado");

    private final String rotulo;

    StatusPacoteBeautyPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public boolean permiteRegistrarSessao() {
        return this == ATIVO || this == PAUSADO;
    }

    public static StatusPacoteBeautyPro deCodigo(String codigo) {
        for (StatusPacoteBeautyPro status : values()) {
            if (status.name().equalsIgnoreCase(codigo)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de pacote Beauty invalido: " + codigo);
    }
}
