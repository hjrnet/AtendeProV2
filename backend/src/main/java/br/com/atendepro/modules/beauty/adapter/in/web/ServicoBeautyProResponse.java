package br.com.atendepro.modules.beauty.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.ServicoBeautyProResult;

public record ServicoBeautyProResponse(
        UUID id,
        String nome,
        String descricao,
        String area,
        int duracaoMinutos,
        BigDecimal precoBase,
        boolean ativo
) {
    public static ServicoBeautyProResponse de(ServicoBeautyProResult result) {
        return new ServicoBeautyProResponse(
                result.id(),
                result.nome(),
                result.descricao(),
                result.area(),
                result.duracaoMinutos(),
                result.precoBase(),
                result.ativo()
        );
    }
}
