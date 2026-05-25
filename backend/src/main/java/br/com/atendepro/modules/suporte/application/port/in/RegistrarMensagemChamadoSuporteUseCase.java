package br.com.atendepro.modules.suporte.application.port.in;

import br.com.atendepro.modules.suporte.application.command.RegistrarMensagemChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.application.result.DetalheChamadoSuporteResult;

public interface RegistrarMensagemChamadoSuporteUseCase {

    DetalheChamadoSuporteResult registrarMensagem(RegistrarMensagemChamadoSuporteCommand command);
}
