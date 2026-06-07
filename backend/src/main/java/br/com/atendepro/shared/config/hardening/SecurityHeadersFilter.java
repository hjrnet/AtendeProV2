package br.com.atendepro.shared.config.hardening;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class SecurityHeadersFilter extends OncePerRequestFilter {

    private static final String HTTPS = "https";
    private static final String CSP_HEADER = "default-src 'self'; base-uri 'self'; frame-ancestors 'none'; object-src 'none';";
    private static final String PERMISSIONS_POLICY_HEADER = "camera=(), geolocation=(), microphone=(), payment=()";

    private final HardeningProperties propriedades;

    public SecurityHeadersFilter(HardeningProperties propriedades) {
        this.propriedades = propriedades;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        aplicarHeadersBasicos(response);
        if (propriedades.enabled() && propriedades.headers().enabled()) {
            aplicarHeadersSeguranca(response, request);
        }
        filterChain.doFilter(request, response);
    }

    private void aplicarHeadersBasicos(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("X-XSS-Protection", "0");
        response.setHeader("Permissions-Policy", PERMISSIONS_POLICY_HEADER);
        response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
        response.setHeader("Content-Security-Policy", CSP_HEADER);
    }

    private void aplicarHeadersSeguranca(HttpServletResponse response, HttpServletRequest request) {
        response.setHeader("X-DNS-Prefetch-Control", "off");
        response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
        response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
        response.setHeader("Cross-Origin-Resource-Policy", "same-origin");

        if (isSeguro(request)) {
            response.setHeader(
                    "Strict-Transport-Security",
                    "max-age=31536000; includeSubDomains; preload"
            );
        }
    }

    private boolean isSeguro(HttpServletRequest request) {
        if (request.isSecure()) {
            return true;
        }
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return HTTPS.equalsIgnoreCase(forwardedProto);
    }
}
