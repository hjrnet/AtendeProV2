package br.com.atendepro.modules.nutri.domain.model;

import java.math.BigDecimal;

public enum ObjetivoNutricionalNutriPro {
    PERDA_DE_PESO("Perda de peso", new BigDecimal("-400.00")),
    GANHO_DE_MASSA("Ganho de massa", new BigDecimal("300.00")),
    MANUTENCAO("Manutenção", BigDecimal.ZERO),
    PERFORMANCE("Performance", BigDecimal.ZERO),
    SAUDE("Saúde", BigDecimal.ZERO);

    private final String rotulo;
    private final BigDecimal ajusteEnergetico;

    ObjetivoNutricionalNutriPro(String rotulo, BigDecimal ajusteEnergetico) {
        this.rotulo = rotulo;
        this.ajusteEnergetico = ajusteEnergetico;
    }

    public String rotulo() {
        return rotulo;
    }

    public BigDecimal ajusteEnergetico() {
        return ajusteEnergetico;
    }

    public static ObjetivoNutricionalNutriPro deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("objetivo nutricional e obrigatorio");
        }
        return ObjetivoNutricionalNutriPro.valueOf(codigo.trim().toUpperCase());
    }
}
