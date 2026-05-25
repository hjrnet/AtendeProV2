package br.com.atendepro.modules.nutri.domain.model;

public enum SexoBiologicoNutriPro {
    FEMININO("Feminino"),
    MASCULINO("Masculino"),
    NAO_INFORMADO("Não informado");

    private final String rotulo;

    SexoBiologicoNutriPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static SexoBiologicoNutriPro deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("sexo biologico da avaliacao nutricional e obrigatorio");
        }
        return SexoBiologicoNutriPro.valueOf(codigo.trim().toUpperCase());
    }
}
