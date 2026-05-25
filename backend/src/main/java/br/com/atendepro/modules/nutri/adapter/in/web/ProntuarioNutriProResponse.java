package br.com.atendepro.modules.nutri.adapter.in.web;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.AcaoProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.PacienteProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.ResumoProntuarioNutriProResult;

public record ProntuarioNutriProResponse(
        UUID empresaId,
        PacienteProntuarioNutriProResponse paciente,
        ResumoProntuarioNutriProResponse resumo,
        List<AcaoProntuarioNutriProResponse> acoesRapidas,
        Instant atualizadoEm
) {

    public static ProntuarioNutriProResponse de(ProntuarioNutriProResult result) {
        return new ProntuarioNutriProResponse(
                result.empresaId(),
                PacienteProntuarioNutriProResponse.de(result.paciente()),
                ResumoProntuarioNutriProResponse.de(result.resumo()),
                result.acoesRapidas().stream().map(AcaoProntuarioNutriProResponse::de).toList(),
                result.atualizadoEm()
        );
    }

    public record PacienteProntuarioNutriProResponse(
            UUID id,
            UUID empresaId,
            String nome,
            String email,
            String telefone,
            LocalDate dataNascimento,
            Integer idade,
            String observacoes,
            boolean ativo,
            Instant atualizadoEm
    ) {

        public static PacienteProntuarioNutriProResponse de(PacienteProntuarioNutriProResult result) {
            return new PacienteProntuarioNutriProResponse(
                    result.id(),
                    result.empresaId(),
                    result.nome(),
                    result.email(),
                    result.telefone(),
                    result.dataNascimento(),
                    result.idade(),
                    result.observacoes(),
                    result.ativo(),
                    result.atualizadoEm()
            );
        }
    }

    public record ResumoProntuarioNutriProResponse(
            long documentos,
            long consultasFuturas,
            long simulacoesPrecificacao,
            long planosAlimentaresAtivos,
            String statusPlanoAlimentar,
            String statusAnamnese,
            String statusAvaliacaoAntropometrica,
            String statusGastoEnergetico,
            String statusExamesLaboratoriais,
            Instant ultimaConsultaEm
    ) {

        public static ResumoProntuarioNutriProResponse de(ResumoProntuarioNutriProResult result) {
            return new ResumoProntuarioNutriProResponse(
                    result.documentos(),
                    result.consultasFuturas(),
                    result.simulacoesPrecificacao(),
                    result.planosAlimentaresAtivos(),
                    result.statusPlanoAlimentar(),
                    result.statusAnamnese(),
                    result.statusAvaliacaoAntropometrica(),
                    result.statusGastoEnergetico(),
                    result.statusExamesLaboratoriais(),
                    result.ultimaConsultaEm()
            );
        }
    }

    public record AcaoProntuarioNutriProResponse(
            String codigo,
            String titulo,
            String descricao,
            String status,
            String statusRotulo,
            boolean destaque
    ) {

        public static AcaoProntuarioNutriProResponse de(AcaoProntuarioNutriProResult result) {
            return new AcaoProntuarioNutriProResponse(
                    result.codigo(),
                    result.titulo(),
                    result.descricao(),
                    result.status().name(),
                    result.status().rotulo(),
                    result.destaque()
            );
        }
    }
}
