package br.com.atendepro.modules.pagamento.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PagamentoAssinatura(
        UUID id,
        UUID empresaId,
        UUID planoId,
        UUID assinaturaInternaId,
        ProvedorPagamento provedor,
        AmbientePagamento ambiente,
        StatusPagamentoAssinatura status,
        String clienteExternoId,
        String assinaturaExternaId,
        String checkoutExternoId,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public PagamentoAssinatura {
        if (id == null || empresaId == null || planoId == null) {
            throw new IllegalArgumentException("identificadores do pagamento sao obrigatorios");
        }
        if (provedor == null || ambiente == null || status == null) {
            throw new IllegalArgumentException("provedor, ambiente e status do pagamento sao obrigatorios");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do pagamento sao obrigatorias");
        }
        if (ambiente == AmbientePagamento.PRODUCAO) {
            throw new IllegalArgumentException("pagamento em producao esta bloqueado nesta release");
        }
    }

    public static PagamentoAssinatura prepararSandbox(
            UUID empresaId,
            UUID planoId,
            UUID assinaturaInternaId,
            String clienteExternoId,
            String assinaturaExternaId,
            String checkoutExternoId,
            Instant agora
    ) {
        return new PagamentoAssinatura(
                UUID.randomUUID(),
                empresaId,
                planoId,
                assinaturaInternaId,
                ProvedorPagamento.ASAAS,
                AmbientePagamento.SANDBOX,
                StatusPagamentoAssinatura.AGUARDANDO_PAGAMENTO,
                clienteExternoId,
                assinaturaExternaId,
                checkoutExternoId,
                agora,
                agora
        );
    }

    public PagamentoAssinatura ativar(Instant agora) {
        return alterarStatus(StatusPagamentoAssinatura.ATIVA, agora);
    }

    public PagamentoAssinatura marcarFalhaPagamento(Instant agora) {
        return alterarStatus(StatusPagamentoAssinatura.FALHA_PAGAMENTO, agora);
    }

    public PagamentoAssinatura cancelar(Instant agora) {
        return alterarStatus(StatusPagamentoAssinatura.CANCELADA, agora);
    }

    private PagamentoAssinatura alterarStatus(StatusPagamentoAssinatura novoStatus, Instant agora) {
        if (status == StatusPagamentoAssinatura.CANCELADA && novoStatus != StatusPagamentoAssinatura.CANCELADA) {
            throw new IllegalArgumentException("assinatura de pagamento cancelada nao pode mudar de status");
        }
        return new PagamentoAssinatura(
                id,
                empresaId,
                planoId,
                assinaturaInternaId,
                provedor,
                ambiente,
                novoStatus,
                clienteExternoId,
                assinaturaExternaId,
                checkoutExternoId,
                criadoEm,
                agora
        );
    }
}
