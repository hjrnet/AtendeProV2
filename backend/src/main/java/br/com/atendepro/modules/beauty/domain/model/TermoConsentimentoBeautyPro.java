package br.com.atendepro.modules.beauty.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TermoConsentimentoBeautyPro(
        UUID id,
        UUID empresaId,
        UUID clienteId,
        UUID protocoloId,
        String titulo,
        String conteudo,
        StatusTermoBeautyPro status,
        boolean aceiteProfissional,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public TermoConsentimentoBeautyPro {
        if (id == null) {
            throw new IllegalArgumentException("id do termo Beauty e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do termo Beauty e obrigatoria");
        }
        if (clienteId == null) {
            throw new IllegalArgumentException("cliente do termo Beauty e obrigatorio");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("titulo do termo Beauty e obrigatorio");
        }
        if (conteudo == null || conteudo.isBlank()) {
            throw new IllegalArgumentException("conteudo do termo Beauty e obrigatorio");
        }
        if (status == null) {
            throw new IllegalArgumentException("status do termo Beauty e obrigatorio");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do termo Beauty sao obrigatorias");
        }
        titulo = titulo.trim();
        conteudo = conteudo.trim();
    }

    public static TermoConsentimentoBeautyPro criar(
            UUID empresaId,
            UUID clienteId,
            UUID protocoloId,
            String titulo,
            String conteudo,
            boolean aceiteProfissional,
            Instant agora
    ) {
        return new TermoConsentimentoBeautyPro(
                UUID.randomUUID(),
                empresaId,
                clienteId,
                protocoloId,
                titulo,
                conteudo,
                aceiteProfissional ? StatusTermoBeautyPro.ACEITO : StatusTermoBeautyPro.GERADO,
                aceiteProfissional,
                agora,
                agora
        );
    }
}
