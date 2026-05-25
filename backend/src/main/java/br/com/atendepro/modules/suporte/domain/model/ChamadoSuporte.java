package br.com.atendepro.modules.suporte.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ChamadoSuporte(
        UUID id,
        UUID empresaId,
        UUID solicitanteUsuarioId,
        String solicitanteNome,
        String solicitanteEmail,
        String titulo,
        String descricao,
        PrioridadeChamadoSuporte prioridade,
        StatusChamadoSuporte status,
        String categoria,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public ChamadoSuporte {
        if (id == null) {
            throw new IllegalArgumentException("id do chamado e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do chamado e obrigatoria");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("titulo do chamado e obrigatorio");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("descricao do chamado e obrigatoria");
        }
        if (prioridade == null) {
            throw new IllegalArgumentException("prioridade do chamado e obrigatoria");
        }
        if (status == null) {
            throw new IllegalArgumentException("status do chamado e obrigatorio");
        }
        if (criadoEm == null) {
            throw new IllegalArgumentException("data de criacao do chamado e obrigatoria");
        }
        if (atualizadoEm == null) {
            throw new IllegalArgumentException("data de atualizacao do chamado e obrigatoria");
        }
        titulo = titulo.trim();
        descricao = descricao.trim();
        solicitanteNome = normalizarTextoOpcional(solicitanteNome);
        solicitanteEmail = normalizarTextoOpcional(solicitanteEmail);
        categoria = normalizarTextoOpcional(categoria);
    }

    public static ChamadoSuporte abrir(
            UUID empresaId,
            UUID solicitanteUsuarioId,
            String solicitanteNome,
            String solicitanteEmail,
            String titulo,
            String descricao,
            PrioridadeChamadoSuporte prioridade,
            String categoria,
            Instant agora
    ) {
        return new ChamadoSuporte(
                UUID.randomUUID(),
                empresaId,
                solicitanteUsuarioId,
                solicitanteNome,
                solicitanteEmail,
                titulo,
                descricao,
                prioridade == null ? PrioridadeChamadoSuporte.MEDIA : prioridade,
                StatusChamadoSuporte.ABERTO,
                categoria,
                agora,
                agora
        );
    }

    public ChamadoSuporte marcarAtualizado(Instant agora) {
        return new ChamadoSuporte(
                id,
                empresaId,
                solicitanteUsuarioId,
                solicitanteNome,
                solicitanteEmail,
                titulo,
                descricao,
                prioridade,
                status,
                categoria,
                criadoEm,
                agora
        );
    }

    public ChamadoSuporte alterarTriagem(
            StatusChamadoSuporte novoStatus,
            PrioridadeChamadoSuporte novaPrioridade,
            Instant agora
    ) {
        return new ChamadoSuporte(
                id,
                empresaId,
                solicitanteUsuarioId,
                solicitanteNome,
                solicitanteEmail,
                titulo,
                descricao,
                novaPrioridade == null ? prioridade : novaPrioridade,
                novoStatus == null ? status : novoStatus,
                categoria,
                criadoEm,
                agora
        );
    }

    private static String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
