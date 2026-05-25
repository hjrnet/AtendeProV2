package br.com.atendepro.modules.beauty.application.result;

public record IndicadorBeautyProResult(
        String codigo,
        String titulo,
        long valor,
        String descricao,
        String status
) {
}
