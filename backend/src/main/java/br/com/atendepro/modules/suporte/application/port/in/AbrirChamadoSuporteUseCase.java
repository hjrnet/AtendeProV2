package br.com.atendepro.modules.suporte.application.port.in;

import br.com.atendepro.modules.suporte.application.command.AbrirChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.application.result.DetalheChamadoSuporteResult;

public interface AbrirChamadoSuporteUseCase {

    DetalheChamadoSuporteResult abrirChamado(AbrirChamadoSuporteCommand command);
}
