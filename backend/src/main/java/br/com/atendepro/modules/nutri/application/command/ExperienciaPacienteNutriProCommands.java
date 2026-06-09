package br.com.atendepro.modules.nutri.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class ExperienciaPacienteNutriProCommands {

    private ExperienciaPacienteNutriProCommands() {
    }

    public record PublicarPlanoAlimentarCommand(UUID empresaId, UUID pacienteId, UUID planoId) {
    }

    public record ConsultarPacienteCommand(UUID empresaId, UUID pacienteId) {
    }

    public record CriarRegistroDiarioCommand(
            UUID empresaId,
            UUID pacienteId,
            String refeicaoNome,
            String texto,
            String evidenciaUrl
    ) {
    }

    public record RevisarRegistroDiarioCommand(
            UUID empresaId,
            UUID pacienteId,
            UUID registroId,
            String parecerProfissional
    ) {
    }

    public record CriarMetaCommand(
            UUID empresaId,
            UUID pacienteId,
            String tipo,
            String descricao,
            BigDecimal valorMeta,
            String unidade,
            LocalDate dataAlvo
    ) {
    }

    public record CriarLembreteCommand(
            UUID empresaId,
            UUID pacienteId,
            String titulo,
            String descricao,
            String horario,
            String frequencia
    ) {
    }

    public record EnviarMensagemCommand(
            UUID empresaId,
            UUID pacienteId,
            String remetenteTipo,
            String remetenteNome,
            String texto,
            String contexto
    ) {
    }

    public record MarcarMensagensLidasCommand(
            UUID empresaId,
            UUID pacienteId,
            String leitor
    ) {
    }
}
