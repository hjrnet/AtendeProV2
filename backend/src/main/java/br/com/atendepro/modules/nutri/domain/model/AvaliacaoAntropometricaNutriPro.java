package br.com.atendepro.modules.nutri.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public record AvaliacaoAntropometricaNutriPro(
        UUID id,
        UUID empresaId,
        UUID pacienteId,
        BigDecimal pesoKg,
        BigDecimal alturaCm,
        int idade,
        SexoBiologicoNutriPro sexo,
        BigDecimal imc,
        ObjetivoNutricionalNutriPro objetivo,
        BigDecimal fatorAtividade,
        BigDecimal gebKcal,
        BigDecimal tmbKcal,
        BigDecimal getKcal,
        BigDecimal metaEnergeticaKcal,
        String formula,
        String aviso,
        String observacoes,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static final String FORMULA_MIFFLIN = "Mifflin-St Jeor (estimativa inicial)";
    public static final String AVISO_CALCULO_ESTIMATIVO = "Cálculos estimativos. O sistema apoia a avaliação, mas a conduta deve ser validada pela nutricionista.";

    public AvaliacaoAntropometricaNutriPro {
        if (id == null) {
            throw new IllegalArgumentException("id da avaliacao antropometrica e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa da avaliacao antropometrica e obrigatoria");
        }
        if (pacienteId == null) {
            throw new IllegalArgumentException("paciente da avaliacao antropometrica e obrigatorio");
        }
        pesoKg = valorPositivo(pesoKg, "peso");
        alturaCm = valorPositivo(alturaCm, "altura");
        if (idade < 1 || idade > 120) {
            throw new IllegalArgumentException("idade da avaliacao nutricional deve ficar entre 1 e 120");
        }
        if (sexo == null) {
            throw new IllegalArgumentException("sexo biologico da avaliacao nutricional e obrigatorio");
        }
        if (objetivo == null) {
            throw new IllegalArgumentException("objetivo nutricional e obrigatorio");
        }
        fatorAtividade = normalizarFatorAtividade(fatorAtividade);
        imc = valorCalculado(imc, "imc");
        gebKcal = valorCalculado(gebKcal, "geb");
        tmbKcal = valorCalculado(tmbKcal, "tmb");
        getKcal = valorCalculado(getKcal, "get");
        metaEnergeticaKcal = valorCalculado(metaEnergeticaKcal, "meta energetica");
        if (formula == null || formula.isBlank()) {
            throw new IllegalArgumentException("formula da avaliacao nutricional e obrigatoria");
        }
        if (aviso == null || aviso.isBlank()) {
            throw new IllegalArgumentException("aviso da avaliacao nutricional e obrigatorio");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas da avaliacao antropometrica sao obrigatorias");
        }
        formula = formula.trim();
        aviso = aviso.trim();
        observacoes = observacoes == null || observacoes.isBlank() ? null : observacoes.trim();
    }

    public static AvaliacaoAntropometricaNutriPro registrar(
            UUID empresaId,
            UUID pacienteId,
            BigDecimal pesoKg,
            BigDecimal alturaCm,
            int idade,
            SexoBiologicoNutriPro sexo,
            ObjetivoNutricionalNutriPro objetivo,
            BigDecimal fatorAtividade,
            String observacoes,
            Instant agora
    ) {
        BigDecimal pesoNormalizado = valorPositivo(pesoKg, "peso");
        BigDecimal alturaNormalizada = valorPositivo(alturaCm, "altura");
        BigDecimal fatorNormalizado = normalizarFatorAtividade(fatorAtividade);
        BigDecimal imcCalculado = calcularImc(pesoNormalizado, alturaNormalizada);
        BigDecimal gebCalculado = calcularGastoBasal(pesoNormalizado, alturaNormalizada, idade, sexo);
        BigDecimal getCalculado = gebCalculado.multiply(fatorNormalizado).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal metaCalculada = getCalculado.add(objetivo.ajusteEnergetico()).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_EVEN);

        return new AvaliacaoAntropometricaNutriPro(
                UUID.randomUUID(),
                empresaId,
                pacienteId,
                pesoNormalizado,
                alturaNormalizada,
                idade,
                sexo,
                imcCalculado,
                objetivo,
                fatorNormalizado,
                gebCalculado,
                gebCalculado,
                getCalculado,
                metaCalculada,
                FORMULA_MIFFLIN,
                AVISO_CALCULO_ESTIMATIVO,
                observacoes,
                agora,
                agora
        );
    }

    private static BigDecimal calcularImc(BigDecimal pesoKg, BigDecimal alturaCm) {
        BigDecimal alturaMetro = alturaCm.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
        return pesoKg.divide(alturaMetro.multiply(alturaMetro), 2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal calcularGastoBasal(
            BigDecimal pesoKg,
            BigDecimal alturaCm,
            int idade,
            SexoBiologicoNutriPro sexo
    ) {
        BigDecimal base = pesoKg.multiply(BigDecimal.valueOf(10))
                .add(alturaCm.multiply(new BigDecimal("6.25")))
                .subtract(BigDecimal.valueOf(idade).multiply(BigDecimal.valueOf(5)))
                .add(ajusteSexo(sexo));
        return base.setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal ajusteSexo(SexoBiologicoNutriPro sexo) {
        if (sexo == SexoBiologicoNutriPro.MASCULINO) {
            return BigDecimal.valueOf(5);
        }
        if (sexo == SexoBiologicoNutriPro.FEMININO) {
            return BigDecimal.valueOf(-161);
        }
        return BigDecimal.valueOf(-78);
    }

    private static BigDecimal valorPositivo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException(campo + " da avaliacao nutricional deve ser positivo");
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal valorCalculado(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " da avaliacao nutricional nao pode ser negativo");
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal normalizarFatorAtividade(BigDecimal fatorAtividade) {
        if (fatorAtividade == null) {
            return new BigDecimal("1.40");
        }
        if (fatorAtividade.compareTo(BigDecimal.ONE) < 0 || fatorAtividade.compareTo(new BigDecimal("3.00")) > 0) {
            throw new IllegalArgumentException("fator de atividade deve ficar entre 1.00 e 3.00");
        }
        return fatorAtividade.setScale(2, RoundingMode.HALF_EVEN);
    }
}
