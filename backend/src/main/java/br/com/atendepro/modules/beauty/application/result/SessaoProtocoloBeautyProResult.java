package br.com.atendepro.modules.beauty.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.SessaoProtocoloBeautyPro;

public record SessaoProtocoloBeautyProResult(
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
    public static SessaoProtocoloBeautyProResult de(SessaoProtocoloBeautyPro sessao) {
        return new SessaoProtocoloBeautyProResult(
                sessao.id(),
                sessao.empresaId(),
                sessao.protocoloId(),
                sessao.clienteId(),
                sessao.agendaCompromissoId(),
                sessao.numeroSessao(),
                sessao.realizadaEm(),
                sessao.descricaoExecucao(),
                sessao.evolucaoCliente(),
                sessao.produtosUtilizados(),
                sessao.orientacoes(),
                sessao.criadoEm()
        );
    }
}
