package br.com.atendepro.modules.empresa.application.port.in;

import br.com.atendepro.modules.empresa.application.result.EmpresaResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarEmpresasUseCase {

    ResultadoPaginado<EmpresaResult> listarEmpresas(Paginacao paginacao);
}
