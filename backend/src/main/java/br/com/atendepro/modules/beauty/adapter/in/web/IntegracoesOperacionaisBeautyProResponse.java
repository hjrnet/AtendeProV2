package br.com.atendepro.modules.beauty.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.beauty.application.result.IntegracoesOperacionaisBeautyProResult;

public record IntegracoesOperacionaisBeautyProResponse(
        List<AgendaBeautyProResponse> agenda,
        List<ServicoBeautyProResponse> servicos,
        List<SimulacaoBeautyProResponse> simulacoes
) {
    public static IntegracoesOperacionaisBeautyProResponse de(IntegracoesOperacionaisBeautyProResult result) {
        return new IntegracoesOperacionaisBeautyProResponse(
                result.agenda().stream().map(AgendaBeautyProResponse::de).toList(),
                result.servicos().stream().map(ServicoBeautyProResponse::de).toList(),
                result.simulacoes().stream().map(SimulacaoBeautyProResponse::de).toList()
        );
    }
}
