package br.com.atendepro.modules.equipamento.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.equipamento.application.result.EquipamentoResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record EquipamentosPaginadosResponse(
        List<EquipamentoResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static EquipamentosPaginadosResponse de(ResultadoPaginado<EquipamentoResult> resultado) {
        return new EquipamentosPaginadosResponse(
                resultado.itens().stream().map(EquipamentoResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
