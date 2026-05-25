package br.com.atendepro.modules.auth.domain.model;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record EmailUsuario(String valor) {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailUsuario {
        Objects.requireNonNull(valor, "email e obrigatorio");
        valor = valor.trim().toLowerCase(Locale.ROOT);
        if (!FORMATO_EMAIL.matcher(valor).matches()) {
            throw new IllegalArgumentException("email invalido");
        }
    }

    public static EmailUsuario de(String valor) {
        return new EmailUsuario(valor);
    }
}
