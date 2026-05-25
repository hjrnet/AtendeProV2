package br.com.atendepro.modules.nutri.domain.model;

public enum StatusAcaoNutriPro {
    DISPONIVEL("Disponível"),
    PREPARADO("Preparado"),
    PROXIMA_TASK("Próxima task");

    private final String rotulo;

    StatusAcaoNutriPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }
}
