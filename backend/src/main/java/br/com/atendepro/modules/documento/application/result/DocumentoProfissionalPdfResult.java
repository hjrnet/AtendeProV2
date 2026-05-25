package br.com.atendepro.modules.documento.application.result;

public record DocumentoProfissionalPdfResult(
        String nomeArquivo,
        String contentType,
        byte[] conteudo
) {

    public DocumentoProfissionalPdfResult {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new IllegalArgumentException("nome do arquivo do documento profissional e obrigatorio");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("tipo do documento profissional e obrigatorio");
        }
        if (conteudo == null || conteudo.length == 0) {
            throw new IllegalArgumentException("conteudo do documento profissional e obrigatorio");
        }
    }
}
