package br.com.atendepro.modules.beauty.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.beauty.application.command.CriarTermoConsentimentoBeautyProCommand;
import jakarta.validation.constraints.NotBlank;

public record CriarTermoConsentimentoBeautyProRequest(
        UUID protocoloId,
        @NotBlank String titulo,
        @NotBlank String conteudo,
        boolean aceiteProfissional
) {
    public CriarTermoConsentimentoBeautyProCommand paraCommand(UUID empresaId, UUID clienteId) {
        return new CriarTermoConsentimentoBeautyProCommand(
                empresaId,
                clienteId,
                protocoloId,
                titulo,
                conteudo,
                aceiteProfissional
        );
    }
}
