package br.com.atendepro.modules.nutri.application.port.in;

import br.com.atendepro.modules.nutri.application.command.BancoAlimentosNutriProCommands.CadastrarItemBancoAlimentosNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.BancoAlimentosNutriProCommands.ConsultarBancoAlimentosNutriProCommand;
import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.BancoAlimentosNutriProResult;
import br.com.atendepro.modules.nutri.application.result.BancoAlimentosNutriProResults.ItemBancoAlimentosNutriProResult;

public interface BancoAlimentosNutriProUseCase {

    BancoAlimentosNutriProResult consultarBancoAlimentos(ConsultarBancoAlimentosNutriProCommand command);

    ItemBancoAlimentosNutriProResult cadastrarItem(CadastrarItemBancoAlimentosNutriProCommand command);
}
