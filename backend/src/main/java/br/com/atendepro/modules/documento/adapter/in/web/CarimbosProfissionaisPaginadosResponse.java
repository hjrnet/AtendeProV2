package br.com.atendepro.modules.documento.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.documento.application.result.CarimboProfissionalResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record CarimbosProfissionaisPaginadosResponse(
        List<CarimboProfissionalResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static CarimbosProfissionaisPaginadosResponse de(ResultadoPaginado<CarimboProfissionalResult> resultado) {
        return new CarimbosProfissionaisPaginadosResponse(
                resultado.itens().stream().map(CarimboProfissionalResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
