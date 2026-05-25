package br.com.atendepro.modules.beauty.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.TermoConsentimentoBeautyProResult;
import br.com.atendepro.modules.beauty.domain.model.StatusTermoBeautyPro;

public record TermoConsentimentoBeautyProResponse(
        UUID id,
        UUID empresaId,
        UUID clienteId,
        UUID protocoloId,
        String titulo,
        String conteudo,
        StatusTermoBeautyPro status,
        String statusRotulo,
        boolean aceiteProfissional,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static TermoConsentimentoBeautyProResponse de(TermoConsentimentoBeautyProResult result) {
        return new TermoConsentimentoBeautyProResponse(
                result.id(),
                result.empresaId(),
                result.clienteId(),
                result.protocoloId(),
                result.titulo(),
                result.conteudo(),
                result.status(),
                result.statusRotulo(),
                result.aceiteProfissional(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
