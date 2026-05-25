package br.com.atendepro.modules.assinatura.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class AssinaturaSaasTest {

    private static final Instant AGORA = Instant.parse("2026-05-25T10:00:00Z");

    @Test
    void deveCriarAssinaturaAtiva() {
        var assinatura = AssinaturaSaas.criar(UUID.randomUUID(), UUID.randomUUID(), AGORA);

        assertThat(assinatura.status()).isEqualTo(AssinaturaStatus.ATIVA);
        assertThat(assinatura.iniciadoEm()).isEqualTo(AGORA);
    }

    @Test
    void deveAlterarPlanoDaAssinaturaAtiva() {
        var assinatura = AssinaturaSaas.criar(UUID.randomUUID(), UUID.randomUUID(), AGORA);
        UUID novoPlanoId = UUID.randomUUID();

        var alterada = assinatura.alterarPlano(novoPlanoId, AGORA.plusSeconds(60));

        assertThat(alterada.planoId()).isEqualTo(novoPlanoId);
        assertThat(alterada.status()).isEqualTo(AssinaturaStatus.ATIVA);
    }

    @Test
    void deveBloquearEDesbloquearAssinatura() {
        var assinatura = AssinaturaSaas.criar(UUID.randomUUID(), UUID.randomUUID(), AGORA);

        var bloqueada = assinatura.bloquear(AGORA.plusSeconds(60));
        var ativa = bloqueada.desbloquear(AGORA.plusSeconds(120));

        assertThat(bloqueada.status()).isEqualTo(AssinaturaStatus.BLOQUEADA);
        assertThat(bloqueada.bloqueadoEm()).isEqualTo(AGORA.plusSeconds(60));
        assertThat(ativa.status()).isEqualTo(AssinaturaStatus.ATIVA);
        assertThat(ativa.bloqueadoEm()).isNull();
    }

    @Test
    void naoDeveAlterarPlanoDeAssinaturaCancelada() {
        var assinatura = AssinaturaSaas.criar(UUID.randomUUID(), UUID.randomUUID(), AGORA)
                .cancelar(AGORA.plusSeconds(60));

        assertThatThrownBy(() -> assinatura.alterarPlano(UUID.randomUUID(), AGORA.plusSeconds(120)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("assinatura cancelada nao pode alterar plano");
    }
}
