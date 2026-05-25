package br.com.atendepro.modules.empresa.application.port.out;

import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarEmpresasPort {

    ResultadoPaginado<EmpresaTenant> listarEmpresas(Paginacao paginacao);
}
