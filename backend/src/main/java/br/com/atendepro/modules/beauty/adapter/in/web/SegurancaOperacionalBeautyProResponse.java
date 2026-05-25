package br.com.atendepro.modules.beauty.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.beauty.application.result.SegurancaOperacionalBeautyProResult;

public record SegurancaOperacionalBeautyProResponse(
        List<TermoConsentimentoBeautyProResponse> termos,
        List<EvidenciaEvolucaoBeautyProResponse> evidencias,
        List<ProdutoUtilizadoBeautyProResponse> produtosUtilizados,
        List<ProdutoBeautyEstoqueResponse> produtosEstoque
) {
    public static SegurancaOperacionalBeautyProResponse de(SegurancaOperacionalBeautyProResult result) {
        return new SegurancaOperacionalBeautyProResponse(
                result.termos().stream().map(TermoConsentimentoBeautyProResponse::de).toList(),
                result.evidencias().stream().map(EvidenciaEvolucaoBeautyProResponse::de).toList(),
                result.produtosUtilizados().stream().map(ProdutoUtilizadoBeautyProResponse::de).toList(),
                result.produtosEstoque().stream().map(ProdutoBeautyEstoqueResponse::de).toList()
        );
    }
}
