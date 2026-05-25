package br.com.atendepro.modules.precificacao.application.result;

public record RelatorioPrecificacaoResult(
        String nomeArquivo,
        String contentType,
        byte[] conteudo
) {

    public RelatorioPrecificacaoResult {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new IllegalArgumentException("nome do arquivo do relatorio e obrigatorio");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("tipo do relatorio e obrigatorio");
        }
        if (conteudo == null || conteudo.length == 0) {
            throw new IllegalArgumentException("conteudo do relatorio e obrigatorio");
        }
    }
}
