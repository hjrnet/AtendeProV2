package br.com.atendepro.modules.assinatura.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.assinatura.application.command.CriarAssinaturaCommand;
import jakarta.validation.constraints.NotNull;

public record CriarAssinaturaRequest(@NotNull UUID empresaId, @NotNull UUID planoId) {

    CriarAssinaturaCommand paraCommand() {
        return new CriarAssinaturaCommand(empresaId, planoId);
    }
}
