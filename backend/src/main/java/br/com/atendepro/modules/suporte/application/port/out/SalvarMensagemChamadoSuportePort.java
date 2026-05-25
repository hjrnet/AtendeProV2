package br.com.atendepro.modules.suporte.application.port.out;

import br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte;

public interface SalvarMensagemChamadoSuportePort {

    void salvarMensagem(MensagemChamadoSuporte mensagem);
}
