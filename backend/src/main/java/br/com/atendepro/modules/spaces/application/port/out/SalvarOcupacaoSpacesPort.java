package br.com.atendepro.modules.spaces.application.port.out;

import br.com.atendepro.modules.spaces.domain.model.OcupacaoSpaces;

public interface SalvarOcupacaoSpacesPort {

    void salvarOcupacao(OcupacaoSpaces ocupacao);
}
