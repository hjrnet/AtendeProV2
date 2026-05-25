package br.com.atendepro.modules.beauty.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.SessaoProtocoloBeautyProResult;

public record SessaoProtocoloBeautyProResponse(
        UUID id,
        UUID empresaId,
        UUID protocoloId,
        UUID clienteId,
        UUID agendaCompromissoId,
        int numeroSessao,
        Instant realizadaEm,
        String descricaoExecucao,
        String evolucaoCliente,
        String produtosUtilizados,
        String orientacoes,
        Instant criadoEm
) {
    public static SessaoProtocoloBeautyProResponse de(SessaoProtocoloBeautyProResult result) {
        return new SessaoProtocoloBeautyProResponse(
                result.id(),
                result.empresaId(),
                result.protocoloId(),
                result.clienteId(),
                result.agendaCompromissoId(),
                result.numeroSessao(),
                result.realizadaEm(),
                result.descricaoExecucao(),
                result.evolucaoCliente(),
                result.produtosUtilizados(),
                result.orientacoes(),
                result.criadoEm()
        );
    }
}
