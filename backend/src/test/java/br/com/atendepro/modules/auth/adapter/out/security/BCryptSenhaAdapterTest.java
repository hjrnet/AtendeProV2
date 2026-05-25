package br.com.atendepro.modules.auth.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BCryptSenhaAdapterTest {

    @Test
    void deveCriptografarEConferirSenha() {
        BCryptSenhaAdapter adapter = new BCryptSenhaAdapter();

        String senhaHash = adapter.criptografarSenha("AtendePro@2026");

        assertThat(senhaHash).isNotEqualTo("AtendePro@2026");
        assertThat(adapter.senhaConfere("AtendePro@2026", senhaHash)).isTrue();
        assertThat(adapter.senhaConfere("SenhaErrada@2026", senhaHash)).isFalse();
    }
}
