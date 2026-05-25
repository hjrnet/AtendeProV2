package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasResumoResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record EmpresasAdminSaasPaginadasResponse(
        List<EmpresaAdminSaasResumoResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    static EmpresasAdminSaasPaginadasResponse de(ResultadoPaginado<EmpresaAdminSaasResumoResult> result) {
        return new EmpresasAdminSaasPaginadasResponse(
                result.itens().stream().map(EmpresaAdminSaasResumoResponse::de).toList(),
                result.totalItens(),
                result.pagina(),
                result.tamanho(),
                result.totalPaginas()
        );
    }
}
