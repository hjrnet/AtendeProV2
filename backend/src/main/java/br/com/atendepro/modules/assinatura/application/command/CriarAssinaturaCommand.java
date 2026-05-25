package br.com.atendepro.modules.assinatura.application.command;

import java.util.UUID;

public record CriarAssinaturaCommand(UUID empresaId, UUID planoId) {

    public CriarAssinaturaCommand {
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa da assinatura e obrigatoria");
        }
        if (planoId == null) {
            throw new IllegalArgumentException("plano da assinatura e obrigatorio");
        }
    }
}
