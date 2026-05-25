package br.com.atendepro.modules.beauty.adapter.in.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.AtalhoBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.ClienteBeautyResumoResult;
import br.com.atendepro.modules.beauty.application.result.IndicadorBeautyProResult;
import br.com.atendepro.modules.beauty.application.result.VisaoBeautyProResult;

public record VisaoBeautyProResponse(
        UUID empresaId,
        String empresaNome,
        String statusOperacional,
        String statusOperacionalRotulo,
        String mensagemStatus,
        List<IndicadorBeautyProResponse> indicadores,
        List<AtalhoBeautyProResponse> atalhosPrioritarios,
        List<AtalhoBeautyProResponse> proximasEvolucoes,
        List<ClienteBeautyResumoResponse> clientesRecentes,
        Instant atualizadoEm
) {

    public static VisaoBeautyProResponse de(VisaoBeautyProResult result) {
        return new VisaoBeautyProResponse(
                result.empresaId(),
                result.empresaNome(),
                result.statusOperacional().name(),
                result.statusOperacional().rotulo(),
                result.statusOperacional().mensagem(),
                result.indicadores().stream().map(IndicadorBeautyProResponse::de).toList(),
                result.atalhosPrioritarios().stream().map(AtalhoBeautyProResponse::de).toList(),
                result.proximasEvolucoes().stream().map(AtalhoBeautyProResponse::de).toList(),
                result.clientesRecentes().stream().map(ClienteBeautyResumoResponse::de).toList(),
                result.atualizadoEm()
        );
    }

    public record IndicadorBeautyProResponse(
            String codigo,
            String titulo,
            long valor,
            String descricao,
            String status
    ) {

        public static IndicadorBeautyProResponse de(IndicadorBeautyProResult result) {
            return new IndicadorBeautyProResponse(
                    result.codigo(),
                    result.titulo(),
                    result.valor(),
                    result.descricao(),
                    result.status()
            );
        }
    }

    public record AtalhoBeautyProResponse(
            String codigo,
            String titulo,
            String descricao,
            String status,
            String destino
    ) {

        public static AtalhoBeautyProResponse de(AtalhoBeautyProResult result) {
            return new AtalhoBeautyProResponse(
                    result.codigo(),
                    result.titulo(),
                    result.descricao(),
                    result.status(),
                    result.destino()
            );
        }
    }

    public record ClienteBeautyResumoResponse(
            UUID id,
            String nome,
            String telefone,
            String observacoes,
            boolean ativo,
            Instant atualizadoEm
    ) {

        public static ClienteBeautyResumoResponse de(ClienteBeautyResumoResult result) {
            return new ClienteBeautyResumoResponse(
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
