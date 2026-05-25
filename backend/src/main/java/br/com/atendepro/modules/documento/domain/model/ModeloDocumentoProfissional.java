package br.com.atendepro.modules.documento.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ModeloDocumentoProfissional(
        UUID id,
        UUID empresaId,
        String nome,
        String descricao,
        TipoDocumentoProfissional tipo,
        String tituloPadrao,
        String conteudoPadrao,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public ModeloDocumentoProfissional {
        if (id == null) {
            throw new IllegalArgumentException("id do modelo de documento profissional e obrigatorio");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome do modelo de documento profissional e obrigatorio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("tipo do modelo de documento profissional e obrigatorio");
        }
        if (tituloPadrao == null || tituloPadrao.isBlank()) {
            throw new IllegalArgumentException("titulo padrao do modelo de documento profissional e obrigatorio");
        }
        if (conteudoPadrao == null || conteudoPadrao.isBlank()) {
            throw new IllegalArgumentException("conteudo padrao do modelo de documento profissional e obrigatorio");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do modelo de documento profissional sao obrigatorias");
        }
        nome = nome.trim();
        descricao = descricao == null ? null : descricao.trim();
        tituloPadrao = tituloPadrao.trim();
        conteudoPadrao = conteudoPadrao.trim();
    }

    public boolean global() {
        return empresaId == null;
    }

    public boolean pertenceAEmpresa(UUID empresaId) {
        return global() || this.empresaId.equals(empresaId);
    }
}
