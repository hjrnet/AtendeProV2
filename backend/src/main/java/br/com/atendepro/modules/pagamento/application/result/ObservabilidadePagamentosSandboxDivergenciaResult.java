package br.com.atendepro.modules.pagamento.application.result;

import java.time.Instant;
import java.util.UUID;

public record ObservabilidadePagamentosSandboxDivergenciaResult(
        UUID pagamentoAssinaturaId,
        UUID empresaId,
        UUID planoId,
        UUID assinaturaInternaId,
        String tipoDivergencia,
        String severidade,
        String descricao,
        String statusAssinatura,
        String statusCobranca,
        String assinaturaExternaId,
        String cobrancaExternaId,
        String eventoTipo,
        Boolean eventoProcessado,
        Instant criadoEm,
        Instant atualizadoEm
) {
}
