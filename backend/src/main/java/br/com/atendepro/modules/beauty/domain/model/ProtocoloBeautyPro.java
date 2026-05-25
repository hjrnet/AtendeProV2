package br.com.atendepro.modules.beauty.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ProtocoloBeautyPro(
        UUID id,
        UUID empresaId,
        UUID clienteId,
        UUID servicoProcedimentoId,
        String nome,
        TipoProtocoloBeautyPro tipo,
        String objetivo,
        int quantidadeSessoesPrevistas,
        int sessoesRealizadas,
        StatusPacoteBeautyPro status,
        String observacoes,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public ProtocoloBeautyPro {
        Objects.requireNonNull(id, "id e obrigatorio");
        Objects.requireNonNull(empresaId, "empresaId e obrigatorio");
        Objects.requireNonNull(clienteId, "clienteId e obrigatorio");
        nome = exigirTexto(nome, "nome");
        Objects.requireNonNull(tipo, "tipo e obrigatorio");
        objetivo = exigirTexto(objetivo, "objetivo");
        if (quantidadeSessoesPrevistas < 1) {
            throw new IllegalArgumentException("quantidadeSessoesPrevistas deve ser maior que zero");
        }
        if (sessoesRealizadas < 0) {
            throw new IllegalArgumentException("sessoesRealizadas nao pode ser negativa");
        }
        Objects.requireNonNull(status, "status e obrigatorio");
        observacoes = limpar(observacoes);
        Objects.requireNonNull(criadoEm, "criadoEm e obrigatorio");
        Objects.requireNonNull(atualizadoEm, "atualizadoEm e obrigatorio");
    }

    public static ProtocoloBeautyPro criar(
            UUID empresaId,
            UUID clienteId,
            UUID servicoProcedimentoId,
            String nome,
            TipoProtocoloBeautyPro tipo,
            String objetivo,
            int quantidadeSessoesPrevistas,
            String observacoes,
            Instant agora
    ) {
        return new ProtocoloBeautyPro(
                UUID.randomUUID(),
                empresaId,
                clienteId,
                servicoProcedimentoId,
                nome,
                tipo,
                objetivo,
                quantidadeSessoesPrevistas,
                0,
                StatusPacoteBeautyPro.ATIVO,
                observacoes,
                agora,
                agora
        );
    }

    public ProtocoloBeautyPro registrarSessao(Instant agora) {
        if (!status.permiteRegistrarSessao()) {
            throw new IllegalStateException("Status do pacote nao permite registrar sessao.");
        }
        int realizadas = sessoesRealizadas + 1;
        StatusPacoteBeautyPro novoStatus = realizadas >= quantidadeSessoesPrevistas
                ? StatusPacoteBeautyPro.CONCLUIDO
                : StatusPacoteBeautyPro.ATIVO;
        return new ProtocoloBeautyPro(
                id,
                empresaId,
                clienteId,
                servicoProcedimentoId,
                nome,
                tipo,
                objetivo,
                quantidadeSessoesPrevistas,
                realizadas,
                novoStatus,
                observacoes,
                criadoEm,
                agora
        );
    }

    private static String exigirTexto(String texto, String campo) {
        String valor = limpar(texto);
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor;
    }

    private static String limpar(String texto) {
        if (texto == null) {
            return null;
        }
        String valor = texto.trim();
        return valor.isEmpty() ? null : valor;
    }
}
