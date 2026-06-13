package br.com.atendepro.modules.pagamento.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class PagamentoAssinaturaTest {

    private static final Instant AGORA = Instant.parse("2026-06-13T10:00:00Z");

    @Test
    void devePrepararPagamentoSandboxAguardandoPagamento() {
        var pagamento = PagamentoAssinatura.prepararSandbox(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "cus_sandbox",
                "sub_sandbox",
                "chk_sandbox",
                AGORA
        );

        assertThat(pagamento.provedor()).isEqualTo(ProvedorPagamento.ASAAS);
        assertThat(pagamento.ambiente()).isEqualTo(AmbientePagamento.SANDBOX);
        assertThat(pagamento.status()).isEqualTo(StatusPagamentoAssinatura.AGUARDANDO_PAGAMENTO);
    }

    @Test
    void naoDevePermitirPagamentoEmProducaoNaR30() {
        assertThatThrownBy(() -> new PagamentoAssinatura(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                ProvedorPagamento.ASAAS,
                AmbientePagamento.PRODUCAO,
                StatusPagamentoAssinatura.PREPARADA,
                null,
                null,
                null,
                AGORA,
                AGORA
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pagamento em producao esta bloqueado nesta release");
    }

    @Test
    void deveAtivarPagamentoRecebido() {
        var pagamento = PagamentoAssinatura.prepararSandbox(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "cus_sandbox",
                "sub_sandbox",
                "chk_sandbox",
                AGORA
        );

        var ativo = pagamento.ativar(AGORA.plusSeconds(60));

        assertThat(ativo.status()).isEqualTo(StatusPagamentoAssinatura.ATIVA);
        assertThat(ativo.atualizadoEm()).isEqualTo(AGORA.plusSeconds(60));
    }
}
