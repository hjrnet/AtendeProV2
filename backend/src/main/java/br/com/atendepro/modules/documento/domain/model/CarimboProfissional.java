package br.com.atendepro.modules.documento.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public record CarimboProfissional(
        UUID id,
        UUID empresaId,
        UUID profissionalId,
        String profissionalNome,
        ConselhoProfissional conselho,
        String uf,
        String numeroRegistro,
        String assinaturaTexto,
        String clinicaNome,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public CarimboProfissional {
        if (id == null) {
            throw new IllegalArgumentException("id do carimbo profissional e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do carimbo profissional e obrigatoria");
        }
        if (profissionalNome == null || profissionalNome.isBlank()) {
            throw new IllegalArgumentException("nome do profissional do carimbo e obrigatorio");
        }
        if (conselho == null) {
            throw new IllegalArgumentException("conselho profissional e obrigatorio");
        }
        if (uf == null || uf.isBlank()) {
            throw new IllegalArgumentException("uf do conselho profissional e obrigatoria");
        }
        if (uf.trim().length() != 2) {
            throw new IllegalArgumentException("uf do conselho profissional deve ter 2 letras");
        }
        if (numeroRegistro == null || numeroRegistro.isBlank()) {
            throw new IllegalArgumentException("numero do registro profissional e obrigatorio");
        }
        if (assinaturaTexto == null || assinaturaTexto.isBlank()) {
            throw new IllegalArgumentException("assinatura do carimbo profissional e obrigatoria");
        }
        if (clinicaNome == null || clinicaNome.isBlank()) {
            throw new IllegalArgumentException("clinica do carimbo profissional e obrigatoria");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do carimbo profissional sao obrigatorias");
        }
        profissionalNome = profissionalNome.trim();
        uf = uf.trim().toUpperCase(Locale.ROOT);
        numeroRegistro = numeroRegistro.trim();
        assinaturaTexto = assinaturaTexto.trim();
        clinicaNome = clinicaNome.trim();
    }

    public static CarimboProfissional criar(
            UUID empresaId,
            UUID profissionalId,
            String profissionalNome,
            ConselhoProfissional conselho,
            String uf,
            String numeroRegistro,
            String assinaturaTexto,
            String clinicaNome,
            Instant agora
    ) {
        return new CarimboProfissional(
                UUID.randomUUID(),
                empresaId,
                profissionalId,
                profissionalNome,
                conselho,
                uf,
                numeroRegistro,
                assinaturaTexto,
                clinicaNome,
                true,
                agora,
                agora
        );
    }
}
