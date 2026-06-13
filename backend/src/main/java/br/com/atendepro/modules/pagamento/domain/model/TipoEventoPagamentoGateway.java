package br.com.atendepro.modules.pagamento.domain.model;

public enum TipoEventoPagamentoGateway {
    CHECKOUT_PREPARADO,
    ASSINATURA_CRIADA,
    PAYMENT_CREATED,
    PAYMENT_RECEIVED,
    PAYMENT_OVERDUE,
    PAYMENT_DELETED,
    PAYMENT_REFUNDED,
    WEBHOOK_REJEITADO,
    FALHA_RECONCILIACAO
}
