package br.com.atendepro.modules.beauty.application.result;

import java.util.List;

public record IntegracoesOperacionaisBeautyProResult(
        List<AgendaBeautyProResult> agenda,
        List<ServicoBeautyProResult> servicos,
        List<SimulacaoBeautyProResult> simulacoes
) {
}
