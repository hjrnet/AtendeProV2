package br.com.atendepro.modules.agenda.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.agenda.application.result.CompromissoAgendaResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record AgendaPaginadaResponse(
        List<CompromissoAgendaResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static AgendaPaginadaResponse de(ResultadoPaginado<CompromissoAgendaResult> resultado) {
        return new AgendaPaginadaResponse(
                resultado.itens().stream().map(CompromissoAgendaResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
