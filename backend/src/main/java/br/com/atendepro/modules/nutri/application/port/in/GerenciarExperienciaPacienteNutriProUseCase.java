package br.com.atendepro.modules.nutri.application.port.in;

import java.util.List;
import java.util.Optional;

import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ArquivarPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ConsultarPacienteCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ConsultarRelatorioGerencialCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarExameAvancadoCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarLembreteCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarMaterialEducativoCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarMetaCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarRegistroDiarioCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.CriarSubstituicaoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.DuplicarPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.EnviarMensagemCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.MarcarMensagensLidasCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.PublicarPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.ReorganizarRefeicoesPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.RevisarRegistroDiarioCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.SalvarModeloPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.SubstituirPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.command.ExperienciaPacienteNutriProCommands.VersionarPlanoAlimentarCommand;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.EvolucaoPacienteResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ExameAvancadoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.LembreteAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.ListaComprasResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MaterialEducativoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MensagemAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.MetaAcompanhamentoResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RelatorioGerencialNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.RegistroDiarioResult;
import br.com.atendepro.modules.nutri.application.result.ExperienciaPacienteNutriProResults.SubstituicaoAlimentarResult;
import br.com.atendepro.modules.nutri.application.result.PlanoAlimentarNutriProResult;

public interface GerenciarExperienciaPacienteNutriProUseCase {

    Optional<PlanoAlimentarNutriProResult> publicarPlanoAlimentar(PublicarPlanoAlimentarCommand command);

    Optional<PlanoAlimentarNutriProResult> substituirPlanoAlimentar(SubstituirPlanoAlimentarCommand command);

    Optional<PlanoAlimentarNutriProResult> duplicarPlanoAlimentar(DuplicarPlanoAlimentarCommand command);

    Optional<PlanoAlimentarNutriProResult> versionarPlanoAlimentar(VersionarPlanoAlimentarCommand command);

    Optional<PlanoAlimentarNutriProResult> salvarModeloPlanoAlimentar(SalvarModeloPlanoAlimentarCommand command);

    Optional<PlanoAlimentarNutriProResult> arquivarPlanoAlimentar(ArquivarPlanoAlimentarCommand command);

    Optional<PlanoAlimentarNutriProResult> reorganizarRefeicoesPlanoAlimentar(ReorganizarRefeicoesPlanoAlimentarCommand command);

    Optional<PlanoAlimentarNutriProResult> consultarPlanoPublicado(ConsultarPacienteCommand command);

    Optional<ListaComprasResult> consultarListaCompras(ConsultarPacienteCommand command);

    List<SubstituicaoAlimentarResult> listarSubstituicoesAlimentares(ConsultarPacienteCommand command, java.util.UUID planoId);

    SubstituicaoAlimentarResult criarSubstituicaoAlimentar(CriarSubstituicaoAlimentarCommand command);

    List<MaterialEducativoResult> listarMateriaisEducativos(ConsultarPacienteCommand command, java.util.UUID planoId);

    MaterialEducativoResult criarMaterialEducativo(CriarMaterialEducativoCommand command);

    List<ExameAvancadoResult> listarExamesAvancados(ConsultarPacienteCommand command);

    ExameAvancadoResult criarExameAvancado(CriarExameAvancadoCommand command);

    RelatorioGerencialNutriProResult consultarRelatorioGerencial(ConsultarRelatorioGerencialCommand command);

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
