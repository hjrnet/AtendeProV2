package br.com.atendepro.modules.beauty.domain.model;

import java.time.Instant;
import java.util.UUID;

public record EvidenciaEvolucaoBeautyPro(
        UUID id,
        UUID empresaId,
        UUID clienteId,
        UUID protocoloId,
        UUID sessaoId,
        TipoPlaceholderEvolucaoBeautyPro tipoPlaceholder,
        String titulo,
        String descricao,
        String observacoesPrivacidade,
        Instant criadoEm
) {

    public EvidenciaEvolucaoBeautyPro {
        if (id == null) {
            throw new IllegalArgumentException("id da evidencia Beauty e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa da evidencia Beauty e obrigatoria");
        }
        if (clienteId == null) {
            throw new IllegalArgumentException("cliente da evidencia Beauty e obrigatorio");
        }
        if (tipoPlaceholder == null) {
            throw new IllegalArgumentException("tipo de placeholder Beauty e obrigatorio");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("titulo da evidencia Beauty e obrigatorio");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("descricao da evidencia Beauty e obrigatoria");
        }
        if (criadoEm == null) {
            throw new IllegalArgumentException("data da evidencia Beauty e obrigatoria");
        }
        titulo = titulo.trim();
        descricao = descricao.trim();
        observacoesPrivacidade = normalizarTextoOpcional(observacoesPrivacidade);
    }

    public static EvidenciaEvolucaoBeautyPro criar(
            UUID empresaId,
            UUID clienteId,
            UUID protocoloId,
            UUID sessaoId,
            TipoPlaceholderEvolucaoBeautyPro tipoPlaceholder,
            String titulo,
            String descricao,
            String observacoesPrivacidade,
            Instant agora
    ) {
        return new EvidenciaEvolucaoBeautyPro(
                UUID.randomUUID(),
                empresaId,
                clienteId,
                protocoloId,
                sessaoId,
                tipoPlaceholder,
                titulo,
                descricao,
                observacoesPrivacidade,
                agora
        );
    }

    public String avisoPrivacidade() {
        return "Placeholder seguro: nenhuma foto real de pessoa foi armazenada neste registro.";
    }

    private static String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
