package br.com.atendepro.modules.auth.adapter.in.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap.admin")
public record UsuarioBootstrapProperties(
        Boolean enabled,
        String nome,
        String email,
        String senha
) {

    public boolean habilitado() {
        return enabled == null || enabled;
    }

    public String nomeConfigurado() {
        return valorOuPadrao(nome, "Administrador AtendePro");
    }

    public String emailConfigurado() {
        return valorOuPadrao(email, "admin@atendepro.local");
    }

    public String senhaConfigurada() {
        return valorOuPadrao(senha, "AtendePro@123");
    }

    private static String valorOuPadrao(String valor, String padrao) {
        if (valor == null || valor.isBlank()) {
            return padrao;
        }
        return valor;
    }
}
