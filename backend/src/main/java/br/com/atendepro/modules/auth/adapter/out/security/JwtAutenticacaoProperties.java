package br.com.atendepro.modules.auth.adapter.out.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record JwtAutenticacaoProperties(
        String jwtIssuer,
        String jwtSecret,
        Integer jwtExpiracaoMinutos,
        Integer jwtRefreshExpiracaoDias
) {

    public String emissor() {
        return valorOuPadrao(jwtIssuer, "atendepro-local");
    }

    public String segredo() {
        return valorOuPadrao(jwtSecret, "troque-este-segredo-localmente");
    }

    public int expiracaoMinutos() {
        if (jwtExpiracaoMinutos == null || jwtExpiracaoMinutos < 5) {
            return 60;
        }
        return jwtExpiracaoMinutos;
    }

    public int refreshExpiracaoDias() {
        if (jwtRefreshExpiracaoDias == null || jwtRefreshExpiracaoDias < 1) {
            return 7;
        }
        return jwtRefreshExpiracaoDias;
    }

    private static String valorOuPadrao(String valor, String padrao) {
        if (valor == null || valor.isBlank()) {
            return padrao;
        }
        return valor;
    }
}
