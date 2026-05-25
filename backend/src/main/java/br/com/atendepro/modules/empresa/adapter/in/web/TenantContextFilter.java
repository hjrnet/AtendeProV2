package br.com.atendepro.modules.empresa.adapter.in.web;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.atendepro.modules.auth.adapter.out.security.JwtAutenticacaoProperties;
import br.com.atendepro.modules.auth.adapter.out.security.JwtChaveAssinaturaFactory;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Profile("!test")
public class TenantContextFilter extends OncePerRequestFilter {

    public static final String HEADER_EMPRESA_ID = "X-Empresa-Id";

    private final JwtDecoder jwtDecoder;

    public TenantContextFilter(JwtAutenticacaoProperties properties) {
        this.jwtDecoder = NimbusJwtDecoder
                .withSecretKey(JwtChaveAssinaturaFactory.criarChaveAssinatura(properties.segredo()))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            resolverContexto(request);
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.limpar();
        }
    }

    private void resolverContexto(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            resolverPorToken(authorization.substring("Bearer ".length()));
            if (TenantContextHolder.contextoAtual().isPresent()) {
                return;
            }
        }

        String empresaIdHeader = request.getHeader(HEADER_EMPRESA_ID);
        if (empresaIdHeader != null && !empresaIdHeader.isBlank()) {
            TenantContextHolder.definir(new TenantContext(UUID.fromString(empresaIdHeader), null, Set.of()));
        }
    }

    private void resolverPorToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Object empresaIdClaim = jwt.getClaims().get("empresaId");
            if (empresaIdClaim == null) {
                return;
            }
            TenantContextHolder.definir(new TenantContext(
                    UUID.fromString(empresaIdClaim.toString()),
                    UUID.fromString(jwt.getSubject()),
                    mapearPerfis(jwt.getClaimAsStringList("perfis"))
            ));
        } catch (JwtException | IllegalArgumentException ignored) {
            TenantContextHolder.limpar();
        }
    }

    private Set<PerfilAcesso> mapearPerfis(List<String> perfis) {
        if (perfis == null) {
            return Set.of();
        }
        return perfis.stream()
                .map(PerfilAcesso::valueOf)
                .collect(Collectors.toUnmodifiableSet());
    }
}
