package br.com.atendepro.modules.nutri.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.AtalhoNutriProResult;
import br.com.atendepro.modules.nutri.application.result.IndicadorNutriProResult;
import br.com.atendepro.modules.nutri.application.result.PacienteNutriResumoResult;
import br.com.atendepro.modules.nutri.application.result.VisaoNutriProResult;

public record VisaoNutriProResponse(
        UUID empresaId,
        String empresaNome,
        String statusOperacional,
        String statusOperacionalRotulo,
        String mensagemStatus,
        List<IndicadorNutriProResponse> indicadores,
        List<AtalhoNutriProResponse> atalhosPrioritarios,
        List<AtalhoNutriProResponse> proximasEvolucoes,
        List<PacienteNutriResumoResponse> pacientesRecentes,
        Instant atualizadoEm
) {

    public static VisaoNutriProResponse de(VisaoNutriProResult result) {
        return new VisaoNutriProResponse(
                result.empresaId(),
                result.empresaNome(),
                result.statusOperacional().name(),
                result.statusOperacional().rotulo(),
                result.statusOperacional().mensagem(),
                result.indicadores().stream().map(IndicadorNutriProResponse::de).toList(),
                result.atalhosPrioritarios().stream().map(AtalhoNutriProResponse::de).toList(),
                result.proximasEvolucoes().stream().map(AtalhoNutriProResponse::de).toList(),
                result.pacientesRecentes().stream().map(PacienteNutriResumoResponse::de).toList(),
                result.atualizadoEm()
        );
    }

    public record IndicadorNutriProResponse(
            String codigo,
            String titulo,
            long valor,
            String descricao,
            String status
    ) {

        public static IndicadorNutriProResponse de(IndicadorNutriProResult result) {
            return new IndicadorNutriProResponse(
                    result.codigo(),
                    result.titulo(),
                    result.valor(),
                    result.descricao(),
                    result.status()
            );
        }
    }

    public record AtalhoNutriProResponse(
            String codigo,
            String titulo,
            String descricao,
            String status,
            String destino
    ) {

        public static AtalhoNutriProResponse de(AtalhoNutriProResult result) {
            return new AtalhoNutriProResponse(
                    result.codigo(),
                    result.titulo(),
                    result.descricao(),
                    result.status(),
                    result.destino()
            );
        }
    }

    public record PacienteNutriResumoResponse(
            UUID id,
            String nome,
            String telefone,
            String observacoes,
            boolean ativo,
            Instant atualizadoEm
    ) {

        public static PacienteNutriResumoResponse de(PacienteNutriResumoResult result) {
            return new PacienteNutriResumoResponse(
                    result.id(),
                    result.nome(),
                    result.telefone(),
                    result.observacoes(),
                    result.ativo(),
                    result.atualizadoEm()
            );
        }
    }
}
