package br.com.atendepro.shared.config.observabilidade;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(0)
public class RequestObservabilidadeFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestObservabilidadeFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = extrairOuCriarCorrelationId(request);
        MDC.put("correlationId", correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        long inicioNano = System.nanoTime();

        try {
            filterChain.doFilter(request, wrappedResponse);
        } finally {
            long duracaoMs = (System.nanoTime() - inicioNano) / 1_000_000;
            log(request, wrappedResponse, correlationId, duracaoMs);
            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }

    private void log(
            HttpServletRequest request,
            ContentCachingResponseWrapper response,
            String correlationId,
            long duracaoMs
    ) {
        TenantContext contexto = TenantContextHolder.contextoAtual().orElse(null);
        String usuarioId = contexto != null ? contexto.usuarioId().toString() : "";
        String empresaId = contexto != null ? contexto.empresaId().toString() : "";

        String uri = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null && !query.isBlank()) {
            uri = uri + "?" + query;
        }

        String mensagem = String.format(
                Locale.ROOT,
                "{\"event\":\"http_request\",\"method\":\"%s\",\"uri\":\"%s\",\"status\":%d,\"duracaoMs\":%d,"
                        + "\"correlationId\":\"%s\",\"empresaId\":\"%s\",\"usuarioId\":\"%s\",\"clienteIp\":\"%s\","
                        + "\"tamResposta\":%d}",
                escapeJson(request.getMethod()),
                escapeJson(uri),
                response.getStatus(),
                duracaoMs,
                escapeJson(correlationId),
                escapeJson(empresaId),
                escapeJson(usuarioId),
                escapeJson(request.getRemoteAddr()),
                response.getContentSize()
        );
        LOGGER.info(mensagem);
    }

    private String extrairOuCriarCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return correlationId;
    }

    private String escapeJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
