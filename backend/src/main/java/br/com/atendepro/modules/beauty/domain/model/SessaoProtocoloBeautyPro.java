package br.com.atendepro.modules.beauty.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SessaoProtocoloBeautyPro(
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

    public SessaoProtocoloBeautyPro {
        Objects.requireNonNull(id, "id e obrigatorio");
        Objects.requireNonNull(empresaId, "empresaId e obrigatorio");
        Objects.requireNonNull(protocoloId, "protocoloId e obrigatorio");
        Objects.requireNonNull(clienteId, "clienteId e obrigatorio");
        if (numeroSessao < 1) {
            throw new IllegalArgumentException("numeroSessao deve ser maior que zero");
        }
        Objects.requireNonNull(realizadaEm, "realizadaEm e obrigatorio");
        descricaoExecucao = exigirTexto(descricaoExecucao, "descricaoExecucao");
        evolucaoCliente = limpar(evolucaoCliente);
        produtosUtilizados = limpar(produtosUtilizados);
        orientacoes = limpar(orientacoes);
        Objects.requireNonNull(criadoEm, "criadoEm e obrigatorio");
    }

    public static SessaoProtocoloBeautyPro criar(
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
            Instant agora
    ) {
        return new SessaoProtocoloBeautyPro(
                UUID.randomUUID(),
                empresaId,
                protocoloId,
                clienteId,
                agendaCompromissoId,
                numeroSessao,
                realizadaEm,
                descricaoExecucao,
                evolucaoCliente,
                produtosUtilizados,
                orientacoes,
                agora
        );
    }

    private static String exigirTexto(String texto, String campo) {
        String valor = limpar(texto);
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor;
    }

    private static String limpar(String texto) {
        if (texto == null) {
            return null;
        }
        String valor = texto.trim();
        return valor.isEmpty() ? null : valor;
    }
}
