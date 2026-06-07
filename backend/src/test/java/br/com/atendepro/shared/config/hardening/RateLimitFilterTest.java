package br.com.atendepro.shared.config.hardening;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;

class RateLimitFilterTest {

    @Test
    void deveBloquearRequisicoesAcimaDoLimitePadraoParaOCliente() throws Exception {
        RateLimitFilter filtro = new RateLimitFilter(criarPropriedades(1, 1));
        FilterChain chain = cadeiaComStatus200();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr("10.0.0.1");

        MockHttpServletResponse responsePrimeira = new MockHttpServletResponse();
        filtro.doFilter(request, responsePrimeira, chain);
        assertThat(responsePrimeira.getStatus()).isEqualTo(HttpStatus.OK.value());

        MockHttpServletResponse responseSegunda = new MockHttpServletResponse();
        filtro.doFilter(request, responseSegunda, chain);
        assertThat(responseSegunda.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(responseSegunda.getHeader("Retry-After")).isNotBlank();
    }

    @Test
    void deveBloquearPorIpDiferenteSemCompartilharJanela() throws Exception {
        RateLimitFilter filtro = new RateLimitFilter(criarPropriedades(1, 1));
        FilterChain chain = cadeiaComStatus200();

        MockHttpServletRequest requestClienteUm = new MockHttpServletRequest("POST", "/api/auth/login");
        requestClienteUm.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse respostaClienteUm = new MockHttpServletResponse();

        MockHttpServletRequest requestClienteDois = new MockHttpServletRequest("POST", "/api/auth/login");
        requestClienteDois.setRemoteAddr("10.0.0.2");
        MockHttpServletResponse respostaClienteDois = new MockHttpServletResponse();

        filtro.doFilter(requestClienteUm, respostaClienteUm, chain);
        filtro.doFilter(requestClienteUm, new MockHttpServletResponse(), chain);
        filtro.doFilter(requestClienteDois, respostaClienteDois, chain);

        assertThat(respostaClienteDois.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private HardeningProperties criarPropriedades(int limitePadrao, int limiteAuth) {
        return new HardeningProperties(
                true,
                new HardeningProperties.RateLimit(
                        true,
                        limitePadrao,
                        limiteAuth,
                        60,
                        List.of("/api/"),
                        List.of("/api/auth/login", "/api/auth/refresh")
                ),
                new HardeningProperties.Headers(true),
                new HardeningProperties.Audit(true, List.of("POST"), List.of("/actuator/health"))
        );
    }

    private FilterChain cadeiaComStatus200() {
        return (request, response) -> ((MockHttpServletResponse) response).setStatus(HttpStatus.OK.value());
    }
}
