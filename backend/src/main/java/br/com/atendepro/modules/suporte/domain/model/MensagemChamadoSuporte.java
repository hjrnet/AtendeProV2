package br.com.atendepro.modules.suporte.domain.model;

import java.time.Instant;
import java.util.UUID;

public record MensagemChamadoSuporte(
        UUID id,
        UUID chamadoId,
        UUID autorUsuarioId,
        String autorNome,
        OrigemMensagemChamadoSuporte origem,
        String mensagem,
        Instant criadoEm
) {

    public MensagemChamadoSuporte {
        if (id == null) {
            throw new IllegalArgumentException("id da mensagem e obrigatorio");
        }
        if (chamadoId == null) {
            throw new IllegalArgumentException("chamado da mensagem e obrigatorio");
        }
        if (origem == null) {
            throw new IllegalArgumentException("origem da mensagem e obrigatoria");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("mensagem do chamado e obrigatoria");
        }
        if (criadoEm == null) {
            throw new IllegalArgumentException("data da mensagem e obrigatoria");
        }
        autorNome = autorNome == null || autorNome.isBlank() ? null : autorNome.trim();
        mensagem = mensagem.trim();
    }

    public static MensagemChamadoSuporte registrar(
            UUID chamadoId,
            UUID autorUsuarioId,
            String autorNome,
            OrigemMensagemChamadoSuporte origem,
            String mensagem,
            Instant agora
    ) {
        return new MensagemChamadoSuporte(
                UUID.randomUUID(),
                chamadoId,
                autorUsuarioId,
                autorNome,
                origem,
                mensagem,
                agora
        );
    }
}
