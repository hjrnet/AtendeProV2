package br.com.atendepro.modules.estoque.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ProdutoEstoqueTest {

    @Test
    void deveCadastrarProdutoComLoteEValidade() {
        ProdutoEstoque produto = ProdutoEstoque.cadastrar(
                UUID.randomUUID(),
                " Seringa descartavel ",
                " Insumos ",
                " LOTE-001 ",
                LocalDate.parse("2026-12-31"),
                " un ",
                new BigDecimal("12.3456"),
                new BigDecimal("1.255"),
                new BigDecimal("3.1234"),
                Instant.parse("2026-05-25T00:00:00Z")
        );

        assertThat(produto.nome()).isEqualTo("Seringa descartavel");
        assertThat(produto.categoria()).isEqualTo("Insumos");
        assertThat(produto.lote()).isEqualTo("LOTE-001");
        assertThat(produto.unidade()).isEqualTo("UN");
        assertThat(produto.quantidadeAtual()).isEqualByComparingTo("12.346");
        assertThat(produto.custoUnitario()).isEqualByComparingTo("1.26");
        assertThat(produto.estoqueMinimo()).isEqualByComparingTo("3.123");
        assertThat(produto.ativo()).isTrue();
    }

    @Test
    void naoDeveCadastrarQuantidadeNegativa() {
        assertThatThrownBy(() -> ProdutoEstoque.cadastrar(
                UUID.randomUUID(),
                "Produto",
                "Insumos",
                "LOTE-001",
                LocalDate.parse("2026-12-31"),
                "UN",
                new BigDecimal("-1.000"),
                new BigDecimal("1.00"),
                BigDecimal.ZERO,
                Instant.parse("2026-05-25T00:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantidade atual do produto de estoque nao pode ser negativa");
    }
}
