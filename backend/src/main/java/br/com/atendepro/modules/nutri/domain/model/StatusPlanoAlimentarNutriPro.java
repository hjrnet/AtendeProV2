package br.com.atendepro.modules.nutri.domain.model;

public enum StatusPlanoAlimentarNutriPro {
    RASCUNHO("Rascunho"),
    ATIVO("Ativo"),
    SUBSTITUIDO("Substituído"),
    ARQUIVADO("Arquivado");

    private final String rotulo;

    StatusPlanoAlimentarNutriPro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public static StatusPlanoAlimentarNutriPro deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("status do plano alimentar e obrigatorio");
        }
        return StatusPlanoAlimentarNutriPro.valueOf(codigo.trim().toUpperCase());
    }
}
