package br.com.atendepro.shared.application.pagination;

import java.util.List;
import java.util.Objects;

public record ResultadoPaginado<T>(List<T> itens, long totalItens, int pagina, int tamanho) {

    public ResultadoPaginado {
        itens = List.copyOf(Objects.requireNonNull(itens, "itens sao obrigatorios"));
        if (totalItens < 0) {
            throw new IllegalArgumentException("total de itens nao pode ser negativo");
        }
        if (pagina < 0) {
            throw new IllegalArgumentException("pagina deve ser maior ou igual a zero");
        }
        if (tamanho < 1) {
            throw new IllegalArgumentException("tamanho deve ser maior que zero");
        }
    }

    public int totalPaginas() {
        return (int) Math.ceil((double) totalItens / tamanho);
    }

    public boolean vazio() {
        return itens.isEmpty();
    }
}
