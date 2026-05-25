package br.com.atendepro.modules.empresa.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import br.com.atendepro.modules.auth.adapter.out.security.JwtAutenticacaoAdapter;
import br.com.atendepro.modules.auth.adapter.out.security.JwtAutenticacaoProperties;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

class TenantContextFilterTest {

    private static final JwtAutenticacaoProperties PROPERTIES = new JwtAutenticacaoProperties(
            "atendepro-test",
            "segredo-de-teste",
            30,
            7,
            30,
            false
    );

    @Test
    void deveResolverTenantPorTokenJwtELimparAoFinal() throws Exception {
        UUID empresaId = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");
        String token = new JwtAutenticacaoAdapter(
                PROPERTIES,
                Clock.systemUTC()
        ).gerarAccessToken(usuario(empresaId)).valor();
        TenantContextFilter filter = new TenantContextFilter(PROPERTIES);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        AtomicReference<TenantContext> contextoDuranteRequest = new AtomicReference<>();

        filter.doFilter(request, new MockHttpServletResponse(), capturarContexto(contextoDuranteRequest));

        assertThat(contextoDuranteRequest.get().empresaId()).isEqualTo(empresaId);
        assertThat(contextoDuranteRequest.get().perfis()).containsExactly(PerfilAcesso.EMPRESA_ADMIN);
        assertThat(TenantContextHolder.contextoAtual()).isEmpty();
    }

    @Test
    void deveResolverTenantPorHeaderQuandoNaoHouverToken() throws Exception {
        UUID empresaId = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");
        TenantContextFilter filter = new TenantContextFilter(PROPERTIES);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(TenantContextFilter.HEADER_EMPRESA_ID, empresaId.toString());
        AtomicReference<TenantContext> contextoDuranteRequest = new AtomicReference<>();

        filter.doFilter(request, new MockHttpServletResponse(), capturarContexto(contextoDuranteRequest));

        assertThat(contextoDuranteRequest.get().empresaId()).isEqualTo(empresaId);
        assertThat(TenantContextHolder.contextoAtual()).isEmpty();
    }

    private FilterChain capturarContexto(AtomicReference<TenantContext> contextoDuranteRequest) {
        return new MockFilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                contextoDuranteRequest.set(TenantContextHolder.contextoAtual().orElseThrow());
            }
        };
    }

    private UsuarioAutenticacao usuario(UUID empresaId) {
        return new UsuarioAutenticacao(
                UUID.fromString("3d0fbc6f-1770-4d05-a0e6-e21efbcaf606"),
                empresaId,
                EmailUsuario.de("admin.empresa@atendepro.local"),
                "Admin Empresa",
                "hash",
                Set.of(PerfilAcesso.EMPRESA_ADMIN),
                true,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }
}
