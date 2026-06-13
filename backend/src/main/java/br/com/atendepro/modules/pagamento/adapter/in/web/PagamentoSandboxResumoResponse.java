package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.pagamento.application.result.PagamentoSandboxResumoResult;

public record PagamentoSandboxResumoResponse(
        UUID pagamentoAssinaturaId,
        UUID empresaId,
        UUID planoId,
        UUID assinaturaInternaId,
        String provedor,
        String ambiente,
        String statusAssinatura,
        String clienteExternoId,
        String assinaturaExternaId,
        String checkoutExternoId,
        UUID cobrancaId,
        String cobrancaExternaId,
        String statusCobranca,
        BigDecimal valor,
        LocalDate vencimento,
        String formaPagamento,
        UUID ultimoEventoId,
        String ultimoEventoTipo,
        boolean ultimoEventoProcessado,
        Instant ultimoEventoEm,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static PagamentoSandboxResumoResponse de(PagamentoSandboxResumoResult result) {
        return new PagamentoSandboxResumoResponse(
                result.pagamentoAssinaturaId(),
                result.empresaId(),
                result.planoId(),
                result.assinaturaInternaId(),
                result.provedor(),
                result.ambiente(),
                result.statusAssinatura(),
                result.clienteExternoId(),
                result.assinaturaExternaId(),
                result.checkoutExternoId(),
                result.cobrancaId(),
                result.cobrancaExternaId(),
                result.statusCobranca(),
                result.valor(),
                result.vencimento(),
                result.formaPagamento(),
                result.ultimoEventoId(),
                result.ultimoEventoTipo(),
                result.ultimoEventoProcessado(),
                result.ultimoEventoEm(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
