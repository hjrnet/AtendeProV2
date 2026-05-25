package br.com.atendepro.modules.documento.domain.model;

public enum AcaoHistoricoDocumentoProfissional {
    SUBSTITUICAO,
    CANCELAMENTO;

    public static AcaoHistoricoDocumentoProfissional deCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("acao do historico do documento profissional e obrigatoria");
        }
        return AcaoHistoricoDocumentoProfissional.valueOf(codigo.trim().toUpperCase());
    }
}
