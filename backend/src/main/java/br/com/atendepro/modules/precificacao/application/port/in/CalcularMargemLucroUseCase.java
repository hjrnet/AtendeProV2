package br.com.atendepro.modules.precificacao.application.port.in;

import br.com.atendepro.modules.precificacao.application.command.CalcularMargemLucroCommand;
import br.com.atendepro.modules.precificacao.application.result.MargemLucroPrecificacaoResult;

public interface CalcularMargemLucroUseCase {

    MargemLucroPrecificacaoResult calcularMargemLucro(CalcularMargemLucroCommand command);
}
