package br.com.atendepro.modules.cliente.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ClientePacienteTest {

    @Test
    void deveCadastrarClientePacienteNormalizandoDocumento() {
        ClientePaciente cliente = ClientePaciente.cadastrar(
                UUID.randomUUID(),
                "  Ana Cliente  ",
                TipoCliente.CLIENTE_PACIENTE,
                AreaCliente.NUTRI,
                "123.456.789-00",
                "ana@test.local",
                "11999999999",
                LocalDate.parse("1990-01-10"),
                "observacao",
                Instant.parse("2026-05-25T00:00:00Z")
        );

        assertThat(cliente.nome()).isEqualTo("Ana Cliente");
        assertThat(cliente.documento()).isEqualTo("12345678900");
        assertThat(cliente.ativo()).isTrue();
    }

    @Test
    void naoDeveCadastrarSemEmpresa() {
        assertThatThrownBy(() -> ClientePaciente.cadastrar(
                null,
                "Ana Cliente",
                TipoCliente.CLIENTE,
                AreaCliente.GERAL,
                null,
                null,
                null,
                null,
                null,
                Instant.parse("2026-05-25T00:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("empresa do cliente e obrigatoria");
    }
}
