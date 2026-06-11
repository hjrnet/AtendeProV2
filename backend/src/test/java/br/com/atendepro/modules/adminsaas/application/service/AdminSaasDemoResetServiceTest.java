package br.com.atendepro.modules.adminsaas.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.adminsaas.application.command.ResetarDemoAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.domain.model.PerfilDemoAdminSaas;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;

class AdminSaasDemoResetServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-11T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void devePrepararResetSemExecutarQuandoNaoConfirmado() {
        AtomicInteger execucoes = new AtomicInteger();
        AdminSaasDemoResetService service = new AdminSaasDemoResetService(
                new PermissaoAcessoService(),
                execucoes::incrementAndGet,
                CLOCK
        );

        var result = service.resetarDemo(new ResetarDemoAdminSaasCommand(
                PerfilDemoAdminSaas.NUTRI,
                false,
                "Preview comercial"
        ));

        assertThat(result.status()).isEqualTo("RESET_PREPARADO");
        assertThat(result.executado()).isFalse();
        assertThat(result.perfilRotulo()).isEqualTo("Nutri Pro");
        assertThat(result.credenciais()).contains("karol.nutri@atendepro.local / AtendePro@123");
        assertThat(execucoes).hasValue(0);
    }

    @Test
    void deveExecutarResetConfirmado() {
        AtomicInteger execucoes = new AtomicInteger();
        AdminSaasDemoResetService service = new AdminSaasDemoResetService(
                new PermissaoAcessoService(),
                execucoes::incrementAndGet,
                CLOCK
        );

        var result = service.resetarDemo(new ResetarDemoAdminSaasCommand(
                PerfilDemoAdminSaas.BEAUTY,
                true,
                "Preparar demo Beauty"
        ));

        assertThat(result.status()).isEqualTo("RESET_EXECUTADO");
        assertThat(result.executado()).isTrue();
        assertThat(result.perfilRotulo()).isEqualTo("Beauty Pro");
        assertThat(result.avisos()).anyMatch(aviso -> aviso.contains("idempotente"));
        assertThat(execucoes).hasValue(1);
    }
}
