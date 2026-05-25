package br.com.atendepro.modules.suporte.application.port.out;

import br.com.atendepro.modules.suporte.domain.model.ChamadoSuporte;

public interface SalvarChamadoSuportePort {

    void salvarChamado(ChamadoSuporte chamado);
}
