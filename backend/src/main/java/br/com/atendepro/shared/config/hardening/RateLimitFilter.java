package br.com.atendepro.shared.config.hardening;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitFilter.class);

    private final HardeningProperties propriedades;
    private final ConcurrentHashMap<String, JanelaToken> janelas = new ConcurrentHashMap<>();

    public RateLimitFilter(HardeningProperties propriedades) {
        this.propriedades = propriedades;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!propriedades.enabled() || !propriedades.rateLimit().enabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!deveAplicarRateLimit(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String identidadeCliente = determinarIdentidadeCliente(request);
        int limite = definirLimite(request.getRequestURI());
        long janelaAtual = Instant.now().getEpochSecond() / Math.max(1, propriedades.rateLimit().windowSeconds());
        JanelaToken estado = janelas.compute(identidadeCliente, (chave, atual) -> {
            if (atual == null || atual.janela != janelaAtual) {
                return new JanelaToken(janelaAtual);
            }
            return atual;
        });

        int total = estado.incrementAndObterTotal();
        long segundosRestantes = definirTempoRestanteSegundos(janelaAtual);

        if (total > limite) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(segundosRestantes));
            response.setHeader("X-RateLimit-Limit", String.valueOf(limite));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setContentType("application/json");
            response.getWriter().write("""
                    {"event":"rate_limit_exceeded","message":"Limite de requisicoes excedido. Tente novamente em alguns segundos.","retryAfterSeconds":%d}
                    """.formatted(segundosRestantes));
            response.getWriter().flush();
            LOGGER.warn(
                    "Rate limit excedeu para cliente={}, caminho={}, limite={}",
                    identidadeCliente,
                    request.getRequestURI(),
                    limite
            );
            return;
        }

        response.setHeader("X-RateLimit-Limit", String.valueOf(limite));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, limite - total)));
        filterChain.doFilter(request, response);
    }

    private boolean deveAplicarRateLimit(String caminho) {
        return propriedades.rateLimit().protectedPathPrefixes().stream().anyMatch(caminho::startsWith);
    }

    private int definirLimite(String caminho) {
        boolean endpointAutenticacao = propriedades.rateLimit().authPathPrefixes().stream().anyMatch(caminho::startsWith);
        return endpointAutenticacao
                ? propriedades.rateLimit().authRequestsPerMinute()
                : propriedades.rateLimit().requestsPerMinute();
    }

    private String determinarIdentidadeCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!isBranco(ip)) {
            ip = ip.split(",")[0].trim();
            return ip;
        }

        String realIp = request.getHeader("X-Real-IP");
        if (!isBranco(realIp)) {
            return realIp.trim();
        }

        return Optional.ofNullable(request.getRemoteAddr()).orElse("anonymous");
    }

    private long definirTempoRestanteSegundos(long janelaAtual) {
        long inicioJanela = janelaAtual * propriedades.rateLimit().windowSeconds();
        long fimJanela = inicioJanela + propriedades.rateLimit().windowSeconds();
        return Math.max(1, fimJanela - Instant.now().getEpochSecond());
    }

    private boolean isBranco(String valor) {
        return valor == null || valor.isBlank();
    }

    private static final class JanelaToken {

        private final long janela;
        private final AtomicInteger total;

        private JanelaToken(long janela) {
            this.janela = janela;
            this.total = new AtomicInteger(0);
        }

        int incrementAndObterTotal() {
            return this.total.incrementAndGet();
        }
    }
}
