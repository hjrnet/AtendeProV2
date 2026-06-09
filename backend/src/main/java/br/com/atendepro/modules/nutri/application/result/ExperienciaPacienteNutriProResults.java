package br.com.atendepro.modules.nutri.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class ExperienciaPacienteNutriProResults {

    private ExperienciaPacienteNutriProResults() {
    }

    public record ListaComprasResult(
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            String objetivoPlano,
            List<GrupoListaComprasResult> grupos,
            Instant geradoEm
    ) {
    }

    public record GrupoListaComprasResult(String categoria, List<ItemListaComprasResult> itens) {
    }

    public record ItemListaComprasResult(
            String nome,
            String categoria,
            BigDecimal quantidade,
            String unidadeMedida,
            String refeicoes,
            String observacoes
    ) {
    }

    public record RegistroDiarioResult(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            UUID planoId,
            String refeicaoNome,
            String texto,
            String evidenciaUrl,
            String statusRevisao,
            String parecerProfissional,
            String criadoPor,
            Instant registradoEm,
            Instant atualizadoEm
    ) {
    }

    public record MetaAcompanhamentoResult(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String tipo,
            String descricao,
            BigDecimal valorMeta,
            String unidade,
            LocalDate dataInicio,
            LocalDate dataAlvo,
            String status,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
    }

    public record LembreteAcompanhamentoResult(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String titulo,
            String descricao,
            String horario,
            String frequencia,
            String status,
            Instant criadoEm,
            Instant atualizadoEm
    ) {
    }

    public record MensagemAcompanhamentoResult(
            UUID id,
            UUID empresaId,
            UUID pacienteId,
            String remetenteTipo,
            String remetenteNome,
            String texto,
            String contexto,
            boolean lidaPeloPaciente,
            boolean lidaPeloProfissional,
            Instant enviadaEm
    ) {
    }

    public record EvolucaoPacienteResult(
            String tipo,
            String titulo,
            String descricao,
            String status,
            Instant data
    ) {
    }
}
