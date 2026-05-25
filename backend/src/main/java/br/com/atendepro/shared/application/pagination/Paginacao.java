package br.com.atendepro.shared.application.pagination;

public record Paginacao(int pagina, int tamanho) {

    public static final int TAMANHO_MAXIMO = 100;

    public Paginacao {
        if (pagina < 0) {
            throw new IllegalArgumentException("pagina deve ser maior ou igual a zero");
        }
        if (tamanho < 1 || tamanho > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("tamanho deve estar entre 1 e " + TAMANHO_MAXIMO);
        }
    }

    public static Paginacao primeiraPagina(int tamanho) {
        return new Paginacao(0, tamanho);
    }

    public int offset() {
        return pagina * tamanho;
    }
}
