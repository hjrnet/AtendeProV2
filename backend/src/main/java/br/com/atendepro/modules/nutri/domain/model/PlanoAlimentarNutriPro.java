package br.com.atendepro.modules.nutri.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PlanoAlimentarNutriPro(
        UUID id,
        UUID empresaId,
        UUID pacienteId,
        String objetivo,
        String descricao,
        StatusPlanoAlimentarNutriPro status,
        List<RefeicaoPlanoAlimentarNutriPro> refeicoes,
        BigDecimal energiaTotalKcal,
        BigDecimal proteinasTotal,
        BigDecimal carboidratosTotal,
        BigDecimal lipidiosTotal,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public PlanoAlimentarNutriPro {
        if (id == null) {
            throw new IllegalArgumentException("id do plano alimentar e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do plano alimentar e obrigatoria");
        }
        if (pacienteId == null) {
            throw new IllegalArgumentException("paciente do plano alimentar e obrigatorio");
        }
        if (objetivo == null || objetivo.isBlank()) {
            throw new IllegalArgumentException("objetivo do plano alimentar e obrigatorio");
        }
        if (status == null) {
            throw new IllegalArgumentException("status do plano alimentar e obrigatorio");
        }
        if (refeicoes == null || refeicoes.isEmpty()) {
            throw new IllegalArgumentException("plano alimentar deve ter ao menos uma refeicao");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do plano alimentar sao obrigatorias");
        }
        objetivo = objetivo.trim();
        descricao = descricao == null || descricao.isBlank() ? null : descricao.trim();
        refeicoes = List.copyOf(refeicoes);
        energiaTotalKcal = total(refeicoes.stream().map(RefeicaoPlanoAlimentarNutriPro::energiaTotalKcal).toList());
        proteinasTotal = total(refeicoes.stream().map(RefeicaoPlanoAlimentarNutriPro::proteinasTotal).toList());
        carboidratosTotal = total(refeicoes.stream().map(RefeicaoPlanoAlimentarNutriPro::carboidratosTotal).toList());
        lipidiosTotal = total(refeicoes.stream().map(RefeicaoPlanoAlimentarNutriPro::lipidiosTotal).toList());
    }

    public static PlanoAlimentarNutriPro montar(
            UUID empresaId,
            UUID pacienteId,
            String objetivo,
            String descricao,
            StatusPlanoAlimentarNutriPro status,
            List<RefeicaoPlanoAlimentarNutriPro> refeicoes,
            Instant agora
    ) {
        return new PlanoAlimentarNutriPro(
                UUID.randomUUID(),
                empresaId,
                pacienteId,
                objetivo,
                descricao,
                status == null ? StatusPlanoAlimentarNutriPro.RASCUNHO : status,
                refeicoes,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                agora,
                agora
        );
    }

    private static BigDecimal total(List<BigDecimal> valores) {
        return valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_EVEN);
    }
}
