package br.com.atendepro.modules.nutri.application.port.in;

import java.util.List;
import java.util.Optional;

import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ConsultarPacienteCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarLembreteCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarMetaCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarRegistroDiarioCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.EnviarMensagemCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.MarcarMensagensLidasCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.PublicarPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.RevisarRegistroDiarioCommand;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.EvolucaoPacienteResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.LembreteAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ListaComprasResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MensagemAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MetaAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RegistroDiarioResult;
import br.com.atendepro.modules.nutri.application.result.PlanoAlimentarNutriProResult;

public interface GerenciarExperienciaPacienteNutriProUseCase {

    Optional<PlanoAlimentarNutriProResult> publicarPlanoAlimentar(PublicarPlanoAlimentarCommand command);

    Optional<PlanoAlimentarNutriProResult> consultarPlanoPublicado(ConsultarPacienteCommand command);

    Optional<ListaComprasResult> consultarListaCompras(ConsultarPacienteCommand command);

    List<RegistroDiarioResult> listarDiarioAlimentar(ConsultarPacienteCommand command);

    RegistroDiarioResult criarRegistroDiario(CriarRegistroDiarioCommand command);

    Optional<RegistroDiarioResult> revisarRegistroDiario(RevisarRegistroDiarioCommand command);

    List<MetaAcompanhamentoResult> listarMetas(ConsultarPacienteCommand command);

    MetaAcompanhamentoResult criarMeta(CriarMetaCommand command);

    List<LembreteAcompanhamentoResult> listarLembretes(ConsultarPacienteCommand command);

    LembreteAcompanhamentoResult criarLembrete(CriarLembreteCommand command);

    List<MensagemAcompanhamentoResult> listarMensagens(ConsultarPacienteCommand command);

    MensagemAcompanhamentoResult enviarMensagem(EnviarMensagemCommand command);

    void marcarMensagensLidas(MarcarMensagensLidasCommand command);

    List<EvolucaoPacienteResult> listarEvolucao(ConsultarPacienteCommand command);
}
