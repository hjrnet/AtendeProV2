package br.com.atendepro.modules.assinatura.application.port.in;

import br.com.atendepro.modules.assinatura.application.command.CriarAssinaturaCommand;
import br.com.atendepro.modules.assinatura.application.result.AssinaturaResult;

public interface CriarAssinaturaUseCase {

    AssinaturaResult criarAssinatura(CriarAssinaturaCommand command);
}
