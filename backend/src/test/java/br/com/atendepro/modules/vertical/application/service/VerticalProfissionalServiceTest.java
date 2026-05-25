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
                .containsExactly(CodigoVerticalProfissional.BEAUTY_PRO, CodigoVerticalProfissional.NUTRI_PRO);
        assertThat(verticais.get(0).capacidades())
                .contains("protocolos de atendimento", "termos de consentimento");
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

    @Test
    void deveDetalharBeautyProComProtocolosETermos() {
        VerticalProfissionalService service = new VerticalProfissionalService(
                new CatalogoVerticalProfissionalAdapter(),
                new PermissaoAcessoService()
        );

        var vertical = service.detalharVertical(CodigoVerticalProfissional.BEAUTY_PRO);

        assertThat(vertical).isPresent();
        assertThat(vertical.get().conselhoProfissional()).isNull();
        assertThat(vertical.get().capacidades()).contains("protocolos de atendimento", "registro de fotos de evolucao");
        assertThat(vertical.get().documentos()).contains("Termo de consentimento");
    }
}
