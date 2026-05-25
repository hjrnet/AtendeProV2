package br.com.atendepro.modules.spaces.application.result;

public record RelatorioSublocacaoSpacesResult(
        String nomeArquivo,
        String contentType,
        byte[] conteudo
) {

    public RelatorioSublocacaoSpacesResult {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new IllegalArgumentException("nome do arquivo do relatorio de sublocacao e obrigatorio");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("tipo do relatorio de sublocacao e obrigatorio");
        }
        if (conteudo == null || conteudo.length == 0) {
            throw new IllegalArgumentException("conteudo do relatorio de sublocacao e obrigatorio");
        }
    }
}
