package br.com.atendepro.modules.cliente.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.cliente.application.result.ClientePacienteResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record ClientesPacientesPaginadosResponse(
        List<ClientePacienteResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static ClientesPacientesPaginadosResponse de(ResultadoPaginado<ClientePacienteResult> resultado) {
        return new ClientesPacientesPaginadosResponse(
                resultado.itens().stream().map(ClientePacienteResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
