package br.com.atendepro.modules.custo.application.port.in;

import br.com.atendepro.modules.custo.application.command.CadastrarCustoAlimentacaoTransporteCommand;
import br.com.atendepro.modules.custo.application.result.CustoAlimentacaoTransporteResult;

public interface CadastrarCustoAlimentacaoTransporteUseCase {

    CustoAlimentacaoTransporteResult cadastrarCustoAlimentacaoTransporte(CadastrarCustoAlimentacaoTransporteCommand command);
}
