package br.com.atendepro.shared.adapter.in.web.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import br.com.atendepro.modules.empresa.domain.exception.AcessoTenantNegadoException;
import br.com.atendepro.shared.domain.exception.BusinessException;
import br.com.atendepro.shared.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveMapearErroDeNegocio() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/teste");

        var response = handler.tratarBusinessException(
                new BusinessException("REGRA_NEGOCIO", "Regra de negocio violada."),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("REGRA_NEGOCIO");
        assertThat(response.getBody().path()).isEqualTo("/api/teste");
    }

    @Test
    void deveMapearAcessoTenantNegado() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/empresas/outra");

        var response = handler.tratarAcessoTenantNegadoException(
                new AcessoTenantNegadoException("TENANT_ACESSO_NEGADO", "Usuario nao possui acesso a esta empresa."),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("TENANT_ACESSO_NEGADO");
        assertThat(response.getBody().path()).isEqualTo("/api/empresas/outra");
    }

    @Test
    void deveMapearErroDeValidacaoComCampos() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/teste");
        var campos = List.of(new ValidationException.CampoErro("nome", "Nome e obrigatorio."));

        var response = handler.tratarValidationException(
                new ValidationException("Dados invalidos.", campos),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("VALIDACAO");
        assertThat(response.getBody().campos()).containsExactly(new CampoErroResponse("nome", "Nome e obrigatorio."));
    }
}
