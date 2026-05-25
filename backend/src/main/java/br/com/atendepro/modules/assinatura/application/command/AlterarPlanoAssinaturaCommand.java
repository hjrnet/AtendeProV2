package br.com.atendepro.modules.assinatura.application.command;

import java.util.UUID;

public record AlterarPlanoAssinaturaCommand(UUID assinaturaId, UUID planoId) {

    public AlterarPlanoAssinaturaCommand {
        if (assinaturaId == null) {
            throw new IllegalArgumentException("assinatura e obrigatoria");
        }
        if (planoId == null) {
            throw new IllegalArgumentException("plano da assinatura e obrigatorio");
        }
    }
}
