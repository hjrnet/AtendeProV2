package br.com.atendepro.modules.documento.domain.model;

public enum TipoDocumentoProfissional {
    DECLARACAO,
    RELATORIO,
    TERMO,
    ORIENTACAO,
    RECIBO,
    SOLICITACAO_EXAMES,
    PRESCRICAO,
    PLANO_ALIMENTAR,
    OUTRO;

    public static TipoDocumentoProfissional deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("tipo do documento profissional e obrigatorio");
        }
        return TipoDocumentoProfissional.valueOf(codigo.trim().toUpperCase());
    }
}
