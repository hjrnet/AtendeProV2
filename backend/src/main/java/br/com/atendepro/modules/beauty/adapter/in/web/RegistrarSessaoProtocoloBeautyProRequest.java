package br.com.atendepro.modules.beauty.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.command.RegistrarSessaoProtocoloBeautyProCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarSessaoProtocoloBeautyProRequest(
        UUID agendaCompromissoId,
        Instant realizadaEm,
        @NotBlank @Size(max = 1200) String descricaoExecucao,
        @Size(max = 1200) String evolucaoCliente,
        @Size(max = 1200) String produtosUtilizados,
        @Size(max = 1200) String orientacoes
) {
    public RegistrarSessaoProtocoloBeautyProCommand paraCommand(UUID empresaId, UUID clienteId, UUID protocoloId) {
        return new RegistrarSessaoProtocoloBeautyProCommand(
                empresaId,
                clienteId,
                protocoloId,
                agendaCompromissoId,
                realizadaEm,
                descricaoExecucao,
                evolucaoCliente,
                produtosUtilizados,
                orientacoes
        );
    }
}
