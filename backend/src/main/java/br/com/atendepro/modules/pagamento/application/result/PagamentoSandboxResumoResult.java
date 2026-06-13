package br.com.atendepro.modules.pagamento.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PagamentoSandboxResumoResult(
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
}
