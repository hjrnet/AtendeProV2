package br.com.atendepro.modules.spaces.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TipoPacoteSublocacaoSpacesTest {

    @Test
    void deveNormalizarCodigoDoTipoDePacote() {
        assertThat(TipoPacoteSublocacaoSpaces.deCodigo("hora")).isEqualTo(TipoPacoteSublocacaoSpaces.HORA);
        assertThat(TipoPacoteSublocacaoSpaces.deCodigo("fixo percentual")).isEqualTo(TipoPacoteSublocacaoSpaces.FIXO_PERCENTUAL);
    }

    @Test
    void naoDeveAceitarTipoInvalido() {
        assertThatThrownBy(() -> TipoPacoteSublocacaoSpaces.deCodigo("assinatura"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tipo de pacote de sublocacao invalido");
    }
}
