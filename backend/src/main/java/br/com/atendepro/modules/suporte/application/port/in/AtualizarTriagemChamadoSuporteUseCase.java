package br.com.atendepro.modules.suporte.application.port.in;

import br.com.atendepro.modules.suporte.application.command.AtualizarTriagemChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.application.result.DetalheChamadoSuporteResult;

public interface AtualizarTriagemChamadoSuporteUseCase {

    DetalheChamadoSuporteResult atualizarTriagem(AtualizarTriagemChamadoSuporteCommand command);
}
