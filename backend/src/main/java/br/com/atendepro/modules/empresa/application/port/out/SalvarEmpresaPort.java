package br.com.atendepro.modules.empresa.application.port.out;

import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;

public interface SalvarEmpresaPort {

    void salvarEmpresa(EmpresaTenant empresa);
}
