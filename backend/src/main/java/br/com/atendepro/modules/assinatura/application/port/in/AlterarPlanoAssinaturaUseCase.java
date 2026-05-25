package br.com.atendepro.modules.assinatura.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.assinatura.application.command.AlterarPlanoAssinaturaCommand;
import br.com.atendepro.modules.assinatura.application.result.AssinaturaResult;

public interface AlterarPlanoAssinaturaUseCase {

    Optional<AssinaturaResult> alterarPlanoAssinatura(AlterarPlanoAssinaturaCommand command);
}
