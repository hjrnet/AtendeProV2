package br.com.atendepro.modules.documento.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CarimboProfissionalTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final Instant AGORA = Instant.parse("2026-05-25T00:00:00Z");

    @Test
    void deveCriarCarimboComDadosNormalizados() {
        CarimboProfissional carimbo = CarimboProfissional.criar(
                EMPRESA_ID,
                UUID.randomUUID(),
                " Dra. Marina ",
                ConselhoProfissional.CRN,
                " sp ",
                " CRN-12345 ",
                " Dra. Marina CRN-12345 ",
                " Clinica Vital ",
                AGORA
        );

        assertThat(carimbo.id()).isNotNull();
        assertThat(carimbo.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(carimbo.profissionalNome()).isEqualTo("Dra. Marina");
        assertThat(carimbo.uf()).isEqualTo("SP");
        assertThat(carimbo.numeroRegistro()).isEqualTo("CRN-12345");
        assertThat(carimbo.assinaturaTexto()).isEqualTo("Dra. Marina CRN-12345");
        assertThat(carimbo.clinicaNome()).isEqualTo("Clinica Vital");
        assertThat(carimbo.ativo()).isTrue();
    }

    @Test
    void naoDeveCriarCarimboComUfInvalida() {
        assertThatThrownBy(() -> CarimboProfissional.criar(
                EMPRESA_ID,
                null,
                "Dra. Marina",
                ConselhoProfissional.CRN,
                "SPO",
                "12345",
                "Assinatura",
                "Clinica",
                AGORA
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("uf do conselho profissional deve ter 2 letras");
    }
}
