package br.com.atendepro.modules.beauty.application.result;

import java.util.List;

public record SegurancaOperacionalBeautyProResult(
        List<TermoConsentimentoBeautyProResult> termos,
        List<EvidenciaEvolucaoBeautyProResult> evidencias,
        List<ProdutoUtilizadoBeautyProResult> produtosUtilizados,
        List<ProdutoBeautyEstoqueResult> produtosEstoque
) {
}
