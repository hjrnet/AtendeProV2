package br.com.atendepro.shared.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StatusControllerTest {

    private final StatusController controller = new StatusController();

    @Test
    void deveConsultarStatusDaAplicacao() {
        StatusController.StatusResponse response = controller.consultarStatus();

        assertThat(response.aplicacao()).isEqualTo("AtendePro Backend");
        assertThat(response.status()).isEqualTo("ONLINE");
        assertThat(response.verificadoEm()).isNotNull();
    }
}
