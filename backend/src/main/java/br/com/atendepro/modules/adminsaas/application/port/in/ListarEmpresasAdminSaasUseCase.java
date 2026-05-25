package br.com.atendepro.modules.adminsaas.application.port.in;

import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasResumoResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarEmpresasAdminSaasUseCase {

    ResultadoPaginado<EmpresaAdminSaasResumoResult> listarEmpresas(Paginacao paginacao, String busca);
}
