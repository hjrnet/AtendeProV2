package br.com.atendepro.modules.nutri.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class ExperienciaPacienteNutriProCommands {

    private ExperienciaPacienteNutriProCommands() {
    }

    public record PublicarPlanoAlimentarCommand(UUID empresaId, UUID pacienteId, UUID planoId) {
    }

    public record DuplicarPlanoAlimentarCommand(UUID empresaId, UUID pacienteId, UUID planoId) {
    }

    public record VersionarPlanoAlimentarCommand(UUID empresaId, UUID pacienteId, UUID planoId) {
    }

    public record SalvarModeloPlanoAlimentarCommand(UUID empresaId, UUID pacienteId, UUID planoId) {
    }

    public record SubstituirPlanoAlimentarCommand(UUID empresaId, UUID pacienteId, UUID planoId) {
    }

    public record ArquivarPlanoAlimentarCommand(UUID empresaId, UUID pacienteId, UUID planoId) {
    }

    public record ReorganizarRefeicoesPlanoAlimentarCommand(UUID empresaId, UUID pacienteId, UUID planoId, List<UUID> refeicaoIds) {
    }

    public record CriarSubstituicaoAlimentarCommand(
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            UUID refeicaoId,
            String alimentoOrigem,
            String alimentoSubstituto,
            String grupo,
            String objetivo,
            String restricaoAlimentar,
            BigDecimal quantidadeEquivalente,
            String unidadeMedida,
            String observacoes
    ) {
    }

    public record CriarMaterialEducativoCommand(
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            String tipo,
            String titulo,
            String objetivo,
            String conteudo,
            String linkAnexo,
            String observacoes
    ) {
    }

    public record CriarExameAvancadoCommand(
            UUID empresaId,
            UUID pacienteId,
            String tipo,
            String nome,
            BigDecimal valor,
            String unidadeMedida,
            LocalDate dataExame,
            String status,
            String observacoes
    ) {
    }

    public record ConsultarRelatorioGerencialCommand(UUID empresaId) {
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
