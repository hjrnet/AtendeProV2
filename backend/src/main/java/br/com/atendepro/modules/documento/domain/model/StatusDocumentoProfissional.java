package br.com.atendepro.modules.documento.domain.model;

public enum StatusDocumentoProfissional {
    RASCUNHO,
    EMITIDO,
    CANCELADO;

    public static StatusDocumentoProfissional deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("status do documento profissional e obrigatorio");
        }
        return StatusDocumentoProfissional.valueOf(codigo.trim().toUpperCase());
    }
}
