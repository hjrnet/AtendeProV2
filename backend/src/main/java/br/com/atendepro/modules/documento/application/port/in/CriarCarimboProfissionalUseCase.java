package br.com.atendepro.modules.documento.application.port.in;

import br.com.atendepro.modules.documento.application.command.CriarCarimboProfissionalCommand;
import br.com.atendepro.modules.documento.application.result.CarimboProfissionalResult;

public interface CriarCarimboProfissionalUseCase {

    CarimboProfissionalResult criarCarimbo(CriarCarimboProfissionalCommand command);
}
