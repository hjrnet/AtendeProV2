package br.com.atendepro.modules.beauty.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.beauty.application.result.FichaEsteticaBeautyProResult;

public record FichasEsteticasBeautyProResponse(List<FichaEsteticaBeautyProResponse> itens) {

    public static FichasEsteticasBeautyProResponse de(List<FichaEsteticaBeautyProResult> results) {
        return new FichasEsteticasBeautyProResponse(results.stream().map(FichaEsteticaBeautyProResponse::de).toList());
    }
}
