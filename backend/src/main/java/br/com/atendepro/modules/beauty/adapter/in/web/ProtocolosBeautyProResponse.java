package br.com.atendepro.modules.beauty.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.beauty.application.result.ProtocoloBeautyProResult;

public record ProtocolosBeautyProResponse(List<ProtocoloBeautyProResponse> itens) {

    public static ProtocolosBeautyProResponse de(List<ProtocoloBeautyProResult> results) {
        return new ProtocolosBeautyProResponse(results.stream().map(ProtocoloBeautyProResponse::de).toList());
    }
}
