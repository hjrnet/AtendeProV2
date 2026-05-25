package br.com.atendepro.modules.assinatura.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.assinatura.application.command.IniciarTrialCommand;
import jakarta.validation.constraints.NotNull;

public record IniciarTrialRequest(@NotNull UUID empresaId, @NotNull UUID planoId) {

    IniciarTrialCommand paraCommand() {
        return new IniciarTrialCommand(empresaId, planoId);
    }
}
