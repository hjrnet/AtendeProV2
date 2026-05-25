package br.com.atendepro.modules.beauty.application.command;

import java.time.Instant;
import java.util.UUID;

public record RegistrarSessaoProtocoloBeautyProCommand(
        UUID empresaId,
        UUID clienteId,
        UUID protocoloId,
        UUID agendaCompromissoId,
        Instant realizadaEm,
        String descricaoExecucao,
        String evolucaoCliente,
        String produtosUtilizados,
        String orientacoes
) {
}
