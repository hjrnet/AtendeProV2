package br.com.atendepro.modules.vertical.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.vertical.application.result.VerticalProfissionalResult;

public record VerticaisProfissionaisResponse(List<VerticalProfissionalResponse> itens) {

    public static VerticaisProfissionaisResponse de(List<VerticalProfissionalResult> results) {
        return new VerticaisProfissionaisResponse(
                results.stream().map(VerticalProfissionalResponse::de).toList()
        );
    }
}
