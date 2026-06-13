package br.com.atendepro.modules.pagamento.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CobrancaPagamento(
        UUID id,
        UUID pagamentoAssinaturaId,
        String cobrancaExternaId,
        StatusCobrancaPagamento status,
        BigDecimal valor,
        LocalDate vencimento,
        String formaPagamento,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public CobrancaPagamento {
        if (id == null || pagamentoAssinaturaId == null) {
            throw new IllegalArgumentException("identificadores da cobranca sao obrigatorios");
        }
        if (status == null || valor == null || criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("status, valor e datas da cobranca sao obrigatorios");
        }
    }

    public static CobrancaPagamento pendente(
            UUID pagamentoAssinaturaId,
            String cobrancaExternaId,
            BigDecimal valor,
            LocalDate vencimento,
            String formaPagamento,
            Instant agora
    ) {
        return new CobrancaPagamento(
                UUID.randomUUID(),
                pagamentoAssinaturaId,
                cobrancaExternaId,
                StatusCobrancaPagamento.PENDENTE,
                valor,
                vencimento,
                formaPagamento,
                agora,
                agora
        );
    }

    public CobrancaPagamento alterarStatus(StatusCobrancaPagamento novoStatus, Instant agora) {
        return new CobrancaPagamento(
                id,
                pagamentoAssinaturaId,
                cobrancaExternaId,
                novoStatus,
                valor,
                vencimento,
                formaPagamento,
                criadoEm,
                agora
        );
    }
}
