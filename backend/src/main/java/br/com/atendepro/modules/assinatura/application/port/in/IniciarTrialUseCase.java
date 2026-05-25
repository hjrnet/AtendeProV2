package br.com.atendepro.modules.assinatura.application.port.in;

import br.com.atendepro.modules.assinatura.application.command.IniciarTrialCommand;
import br.com.atendepro.modules.assinatura.application.result.TrialResult;

public interface IniciarTrialUseCase {

    TrialResult iniciarTrial(IniciarTrialCommand command);
}
