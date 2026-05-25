package br.com.atendepro.modules.assinatura.application.port.out;

import br.com.atendepro.modules.assinatura.domain.model.AssinaturaSaas;

public interface SalvarAssinaturaPort {

    void salvarAssinatura(AssinaturaSaas assinatura);
}
