package br.com.atendepro.modules.documento.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DocumentoProfissional(
        UUID id,
        UUID empresaId,
        UUID clientePacienteId,
        UUID profissionalId,
        String profissionalNome,
        String titulo,
        TipoDocumentoProfissional tipo,
        String conteudo,
        StatusDocumentoProfissional status,
        int versao,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public DocumentoProfissional {
        if (id == null) {
            throw new IllegalArgumentException("id do documento profissional e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do documento profissional e obrigatoria");
        }
        if (profissionalNome == null || profissionalNome.isBlank()) {
            throw new IllegalArgumentException("nome do profissional do documento e obrigatorio");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("titulo do documento profissional e obrigatorio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("tipo do documento profissional e obrigatorio");
        }
        if (conteudo == null || conteudo.isBlank()) {
            throw new IllegalArgumentException("conteudo do documento profissional e obrigatorio");
        }
        if (status == null) {
            throw new IllegalArgumentException("status do documento profissional e obrigatorio");
        }
        if (versao < 1) {
            throw new IllegalArgumentException("versao do documento profissional deve ser positiva");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do documento profissional sao obrigatorias");
        }
        profissionalNome = profissionalNome.trim();
        titulo = titulo.trim();
        conteudo = conteudo.trim();
    }

    public static DocumentoProfissional criar(
            UUID empresaId,
            UUID clientePacienteId,
            UUID profissionalId,
            String profissionalNome,
            String titulo,
            TipoDocumentoProfissional tipo,
            String conteudo,
            StatusDocumentoProfissional status,
            Instant agora
    ) {
        return new DocumentoProfissional(
                UUID.randomUUID(),
                empresaId,
                clientePacienteId,
                profissionalId,
                profissionalNome,
                titulo,
                tipo,
                conteudo,
                status == null ? StatusDocumentoProfissional.RASCUNHO : status,
                1,
                true,
                agora,
                agora
        );
    }
}
