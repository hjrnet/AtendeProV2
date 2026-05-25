package br.com.atendepro.modules.vertical.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.vertical.adapter.out.catalogo.CatalogoVerticalProfissionalAdapter;
import br.com.atendepro.modules.vertical.domain.model.CodigoVerticalProfissional;

class VerticalProfissionalServiceTest {

    @Test
    void deveListarNutriProNoCatalogoInicial() {
        VerticalProfissionalService service = new VerticalProfissionalService(
                new CatalogoVerticalProfissionalAdapter(),
                new PermissaoAcessoService()
        );

        var verticais = service.listarVerticais();

        assertThat(verticais)
                .extracting("codigo")
                .containsExactly(CodigoVerticalProfissional.NUTRI_PRO);
        assertThat(verticais.get(0).capacidades())
                .contains("plano alimentar", "diario alimentar", "solicitacao de exames");
    }

    @Test
    void deveDetalharNutriProComConselhoCrn() {
        VerticalProfissionalService service = new VerticalProfissionalService(
                new CatalogoVerticalProfissionalAdapter(),
                new PermissaoAcessoService()
        );

        var vertical = service.detalharVertical(CodigoVerticalProfissional.NUTRI_PRO);

        assertThat(vertical).isPresent();
        assertThat(vertical.get().conselhoProfissional()).isEqualTo("CRN");
        assertThat(vertical.get().documentos()).contains("Plano alimentar imprimivel");
    }
}
