package br.com.atendepro.modules.precificacao.application.port.in;

import br.com.atendepro.modules.precificacao.application.command.CalcularCustoRealCommand;
import br.com.atendepro.modules.precificacao.application.result.CustoRealPrecificacaoResult;

public interface CalcularCustoRealUseCase {

    CustoRealPrecificacaoResult calcularCustoReal(CalcularCustoRealCommand command);
}
