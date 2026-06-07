package br.com.atendepro.shared.config.observabilidade;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;

class RequestObservabilidadeFilterTest {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Test
    void deveInjetarCorrelationIdQuandoNaoForInformado() throws Exception {
        RequestObservabilidadeFilter filtro = new RequestObservabilidadeFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filtro.doFilter(request, response, chainSemStatus());

        assertThat(response.getHeader(CORRELATION_ID_HEADER)).isNotBlank();
        assertThat(MDC.get("correlationId")).isNull();
    }

    @Test
    void deveReutilizarCorrelationIdInformadoNoHeader() throws Exception {
        String correlationIdEsperado = "corr-id-teste";
        RequestObservabilidadeFilter filtro = new RequestObservabilidadeFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/health");
        request.addHeader(CORRELATION_ID_HEADER, correlationIdEsperado);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filtro.doFilter(request, response, chainSemStatus());

        assertThat(response.getHeader(CORRELATION_ID_HEADER)).isEqualTo(correlationIdEsperado);
        assertThat(MDC.get("correlationId")).isNull();
    }

    private FilterChain chainSemStatus() {
        return (req, res) -> {
            if (res instanceof MockHttpServletResponse mockResponse) {
                mockResponse.setStatus(200);
            }
        };
    }
}
