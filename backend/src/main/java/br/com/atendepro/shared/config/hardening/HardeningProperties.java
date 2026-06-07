package br.com.atendepro.shared.config.hardening;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.hardening")
public record HardeningProperties(
        boolean enabled,
        RateLimit rateLimit,
        Headers headers,
        Audit audit
) {

    private static final int LIMITE_PADRAO_POR_MINUTO = 120;
    private static final int LIMITE_AUTH_POR_MINUTO = 30;
    private static final int JANELA_SEGUNDOS_PADRAO = 60;
    private static final List<String> PREFIXOS_PATHS_PROTEGIDOS = List.of("/api/");
    private static final List<String> PREFIXOS_AUTH_PROTEGIDOS = List.of(
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/password/forgot",
            "/api/auth/password/reset"
    );
    private static final List<String> METODOS_AUDITORIA_PADRAO = List.of("POST", "PUT", "PATCH", "DELETE");
    private static final List<String> IGNORAR_PREFIXOS_AUDITORIA_PADRAO = List.of();

    public HardeningProperties {
        enabled = true;
        if (rateLimit == null) {
            rateLimit = new RateLimit(
                    true,
                    LIMITE_PADRAO_POR_MINUTO,
                    LIMITE_AUTH_POR_MINUTO,
                    JANELA_SEGUNDOS_PADRAO,
                    PREFIXOS_PATHS_PROTEGIDOS,
                    PREFIXOS_AUTH_PROTEGIDOS
            );
        }
        if (headers == null) {
            headers = new Headers(true);
        }
        if (audit == null) {
            audit = new Audit(
                    true,
                    METODOS_AUDITORIA_PADRAO,
                    IGNORAR_PREFIXOS_AUDITORIA_PADRAO
            );
        }
    }

    public record RateLimit(
            boolean enabled,
            int requestsPerMinute,
            int authRequestsPerMinute,
            int windowSeconds,
            List<String> protectedPathPrefixes,
            List<String> authPathPrefixes
    ) {

        private static final int LIMITE_PADRAO_POR_MINUTO = 120;
        private static final int LIMITE_AUTH_POR_MINUTO = 30;
        private static final int JANELA_SEGUNDOS_PADRAO = 60;

        public RateLimit {
            enabled = true;
            if (requestsPerMinute <= 0) {
                requestsPerMinute = LIMITE_PADRAO_POR_MINUTO;
            }
            if (authRequestsPerMinute <= 0) {
                authRequestsPerMinute = LIMITE_AUTH_POR_MINUTO;
            }
            if (windowSeconds <= 0) {
                windowSeconds = JANELA_SEGUNDOS_PADRAO;
            }
            if (protectedPathPrefixes == null || protectedPathPrefixes.isEmpty()) {
                protectedPathPrefixes = List.of("/api/");
            }
            if (authPathPrefixes == null || authPathPrefixes.isEmpty()) {
                authPathPrefixes = List.of("/api/auth/login", "/api/auth/refresh");
            }
        }
    }

    public record Headers(
            boolean enabled
    ) {

        public Headers {
            enabled = true;
        }
    }

    public record Audit(
            boolean enabled,
            List<String> methods,
            List<String> ignorePrefixes
    ) {

        public Audit {
            enabled = true;
            if (methods == null || methods.isEmpty()) {
                methods = List.of("POST", "PUT", "PATCH", "DELETE");
            }
            if (ignorePrefixes == null) {
                ignorePrefixes = List.of();
            }
        }
    }
}
