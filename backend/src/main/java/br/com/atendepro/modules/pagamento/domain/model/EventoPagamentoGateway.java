package br.com.atendepro.modules.pagamento.domain.model;

import java.time.Instant;
import java.util.UUID;

public record EventoPagamentoGateway(
        UUID id,
        UUID pagamentoAssinaturaId,
        ProvedorPagamento provedor,
        AmbientePagamento ambiente,
        TipoEventoPagamentoGateway tipo,
        String eventoExternoId,
        String referenciaExternaId,
        String payloadSanitizado,
        boolean processado,
        Instant criadoEm
) {

    public EventoPagamentoGateway {
        if (id == null || provedor == null || ambiente == null || tipo == null || criadoEm == null) {
            throw new IllegalArgumentException("dados obrigatorios do evento de pagamento ausentes");
        }
        if (ambiente == AmbientePagamento.PRODUCAO) {
            throw new IllegalArgumentException("webhook de producao esta bloqueado nesta release");
        }
    }

    public static EventoPagamentoGateway recebido(
            UUID pagamentoAssinaturaId,
            AmbientePagamento ambiente,
            TipoEventoPagamentoGateway tipo,
            String eventoExternoId,
            String referenciaExternaId,
            String payloadSanitizado,
            boolean processado,
            Instant agora
    ) {
        return new EventoPagamentoGateway(
                UUID.randomUUID(),
                pagamentoAssinaturaId,
                ProvedorPagamento.ASAAS,
                ambiente,
                tipo,
                eventoExternoId,
                referenciaExternaId,
                payloadSanitizado,
                processado,
                agora
        );
    }
}
