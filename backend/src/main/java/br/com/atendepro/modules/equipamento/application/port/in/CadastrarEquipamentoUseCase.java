package br.com.atendepro.modules.equipamento.application.port.in;

import br.com.atendepro.modules.equipamento.application.command.CadastrarEquipamentoCommand;
import br.com.atendepro.modules.equipamento.application.result.EquipamentoResult;

public interface CadastrarEquipamentoUseCase {

    EquipamentoResult cadastrarEquipamento(CadastrarEquipamentoCommand command);
}
