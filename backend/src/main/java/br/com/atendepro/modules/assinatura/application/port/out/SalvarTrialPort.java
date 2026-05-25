package br.com.atendepro.modules.assinatura.application.port.out;

import br.com.atendepro.modules.assinatura.domain.model.TrialAssinatura;

public interface SalvarTrialPort {

    void salvarTrial(TrialAssinatura trial);
}
