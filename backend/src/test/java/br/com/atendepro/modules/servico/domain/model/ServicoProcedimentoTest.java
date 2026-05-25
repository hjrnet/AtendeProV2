package br.com.atendepro.modules.servico.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;

class ServicoProcedimentoTest {

    @Test
    void deveCadastrarServicoComPrecoNormalizado() {
        ServicoProcedimento servico = ServicoProcedimento.cadastrar(
                UUID.randomUUID(),
                " Consulta Nutricional ",
                "avaliacao",
                AreaCliente.NUTRI,
                60,
                new BigDecimal("199.999"),
                Instant.parse("2026-05-25T00:00:00Z")
        );

        assertThat(servico.nome()).isEqualTo("Consulta Nutricional");
        assertThat(servico.precoBase()).isEqualByComparingTo("200.00");
        assertThat(servico.ativo()).isTrue();
    }

    @Test
    void naoDeveCadastrarPrecoNegativo() {
        assertThatThrownBy(() -> ServicoProcedimento.cadastrar(
                UUID.randomUUID(),
                "Consulta",
                null,
                AreaCliente.GERAL,
                60,
                new BigDecimal("-1.00"),
                Instant.parse("2026-05-25T00:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("preco base do servico nao pode ser negativo");
    }
}
