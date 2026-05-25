package br.com.atendepro.modules.assinatura.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.assinatura.application.command.AlterarPlanoAssinaturaCommand;
import jakarta.validation.constraints.NotNull;

public record AlterarPlanoAssinaturaRequest(@NotNull UUID planoId) {

    AlterarPlanoAssinaturaCommand paraCommand(UUID assinaturaId) {
        return new AlterarPlanoAssinaturaCommand(assinaturaId, planoId);
    }
}
