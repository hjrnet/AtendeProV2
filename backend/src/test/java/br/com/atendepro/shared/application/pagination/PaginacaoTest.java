package br.com.atendepro.shared.application.pagination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class PaginacaoTest {

    @Test
    void deveCalcularOffset() {
        Paginacao paginacao = new Paginacao(2, 20);

        assertThat(paginacao.offset()).isEqualTo(40);
    }

    @Test
    void deveCalcularTotalPaginas() {
        ResultadoPaginado<String> resultado = new ResultadoPaginado<>(List.of("a", "b"), 21, 0, 10);

        assertThat(resultado.totalPaginas()).isEqualTo(3);
        assertThat(resultado.vazio()).isFalse();
    }

    @Test
    void naoDevePermitirTamanhoInvalido() {
        assertThatThrownBy(() -> new Paginacao(0, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
