package br.com.atendepro.modules.servico.application.port.out;

import br.com.atendepro.modules.servico.domain.model.ServicoProcedimento;

public interface SalvarServicoProcedimentoPort {

    void salvarServicoProcedimento(ServicoProcedimento servico);
}
