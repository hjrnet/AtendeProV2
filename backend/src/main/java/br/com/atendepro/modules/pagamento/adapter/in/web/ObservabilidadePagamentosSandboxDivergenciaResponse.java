package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.pagamento.application.result.ObservabilidadePagamentosSandboxDivergenciaResult;

public record ObservabilidadePagamentosSandboxDivergenciaResponse(
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

    public static ObservabilidadePagamentosSandboxDivergenciaResponse de(ObservabilidadePagamentosSandboxDivergenciaResult result) {
        return new ObservabilidadePagamentosSandboxDivergenciaResponse(
                result.pagamentoAssinaturaId(),
                result.empresaId(),
                result.planoId(),
                result.assinaturaInternaId(),
                result.tipoDivergencia(),
                result.severidade(),
                result.descricao(),
                result.statusAssinatura(),
                result.statusCobranca(),
                result.assinaturaExternaId(),
                result.cobrancaExternaId(),
                result.eventoTipo(),
                result.eventoProcessado(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
