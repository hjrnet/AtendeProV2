package br.com.atendepro.modules.assinatura.application.command;

import java.util.UUID;

public record IniciarTrialCommand(UUID empresaId, UUID planoId) {

    public IniciarTrialCommand {
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do trial e obrigatoria");
        }
        if (planoId == null) {
            throw new IllegalArgumentException("plano do trial e obrigatorio");
        }
    }
}
