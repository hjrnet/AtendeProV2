package br.com.atendepro.modules.suporte.application.port.out;

import br.com.atendepro.modules.suporte.domain.model.ChamadoSuporte;

public interface AtualizarChamadoSuportePort {

    void atualizarChamado(ChamadoSuporte chamado);
}
