package br.com.atendepro.modules.beauty.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.domain.model.StatusTermoBeautyPro;
import br.com.atendepro.modules.beauty.domain.model.TermoConsentimentoBeautyPro;

public record TermoConsentimentoBeautyProResult(
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
    public static TermoConsentimentoBeautyProResult de(TermoConsentimentoBeautyPro termo) {
        return new TermoConsentimentoBeautyProResult(
                termo.id(),
                termo.empresaId(),
                termo.clienteId(),
                termo.protocoloId(),
                termo.titulo(),
                termo.conteudo(),
                termo.status(),
                termo.status().rotulo(),
                termo.aceiteProfissional(),
                termo.criadoEm(),
                termo.atualizadoEm()
        );
    }
}
