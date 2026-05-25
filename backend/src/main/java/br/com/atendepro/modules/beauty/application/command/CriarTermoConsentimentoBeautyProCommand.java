package br.com.atendepro.modules.beauty.application.command;

import java.util.UUID;

public record CriarTermoConsentimentoBeautyProCommand(
        UUID empresaId,
        UUID clienteId,
        UUID protocoloId,
        String titulo,
        String conteudo,
        boolean aceiteProfissional
) {
}
