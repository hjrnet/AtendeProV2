package br.com.atendepro.shared.config.hardening;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;

class SecurityHeadersFilterTest {

    @Test
    void deveAplicarCabecalhosDeSegurancaPadrao() throws Exception {
        var filtro = new SecurityHeadersFilter(criarPropriedades());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/health");
        request.setScheme("http");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filtro.doFilter(request, response, cadeiaSimples());

        assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(response.getHeader("X-Frame-Options")).isEqualTo("DENY");
        assertThat(response.getHeader("Referrer-Policy")).isEqualTo("strict-origin-when-cross-origin");
        assertThat(response.getHeader("Content-Security-Policy")).isNotBlank();
        assertThat(response.getHeader("Permissions-Policy")).isNotBlank();
        assertThat(response.getHeader("X-DNS-Prefetch-Control")).isEqualTo("off");
    }

    private HardeningProperties criarPropriedades() {
        return new HardeningProperties(
                true,
                new HardeningProperties.RateLimit(
                        true,
                        120,
                        30,
                        60,
                        List.of("/api/"),
                        List.of("/api/auth/login", "/api/auth/refresh")
                ),
                new HardeningProperties.Headers(true),
                new HardeningProperties.Audit(true, List.of("POST"), List.of())
        );
    }

    private FilterChain cadeiaSimples() {
        return (request, response) -> {};
    }
}
