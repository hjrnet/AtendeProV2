package br.com.atendepro.shared.config.hardening;

import java.io.IOException;
import java.util.Locale;

import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class AuditFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    private final HardeningProperties propriedades;

    public AuditFilter(HardeningProperties propriedades) {
        this.propriedades = propriedades;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!propriedades.enabled() || !propriedades.audit().enabled() || !deveAuditar(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        long inicioNano = System.nanoTime();
        ContentCachingResponseWrapper wrapped = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, wrapped);
        } finally {
            long duracaoMs = (System.nanoTime() - inicioNano) / 1_000_000;
            logAuditoria(request, wrapped, duracaoMs);
            wrapped.copyBodyToResponse();
        }
    }

    private void logAuditoria(HttpServletRequest request, ContentCachingResponseWrapper response, long duracaoMs) {
        TenantContext contexto = TenantContextHolder.contextoAtual().orElse(null);
        String empresaId = contexto != null && contexto.empresaId() != null ? contexto.empresaId().toString() : "";
        String usuarioId = contexto != null && contexto.usuarioId() != null ? contexto.usuarioId().toString() : "";

        String evento = String.format(
                Locale.ROOT,
                "{\"event\":\"auditoria\",\"method\":\"%s\",\"uri\":\"%s\",\"status\":%d,\"duracaoMs\":%d,"
                        + "\"correlationId\":\"%s\",\"empresaId\":\"%s\",\"usuarioId\":\"%s\",\"clienteIp\":\"%s\"}",
                escapeJson(request.getMethod()),
                escapeJson(request.getRequestURI()),
                response.getStatus(),
                duracaoMs,
                escapeJson(request.getHeader(CORRELATION_ID_HEADER)),
                escapeJson(empresaId),
                escapeJson(usuarioId),
                escapeJson(request.getRemoteAddr())
        );
        LOGGER.info(evento);
    }

    private boolean deveAuditar(HttpServletRequest request) {
        String metodo = request.getMethod().toUpperCase(Locale.ROOT);
        if (!propriedades.audit().methods().contains(metodo)) {
            return false;
        }
        String uri = request.getRequestURI();
        return propriedades.audit().ignorePrefixes().stream().noneMatch(uri::startsWith);
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
