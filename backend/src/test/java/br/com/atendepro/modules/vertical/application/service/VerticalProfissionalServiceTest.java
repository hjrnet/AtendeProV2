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
                .containsExactly(
                        CodigoVerticalProfissional.BEAUTY_PRO,
                        CodigoVerticalProfissional.BIOMED_PRO,
                        CodigoVerticalProfissional.FISIO_PRO,
                        CodigoVerticalProfissional.NUTRI_PRO,
                        CodigoVerticalProfissional.PSICO_PRO
                );
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

    @Test
    void deveDetalharBiomedProComCrbmERastreabilidade() {
        VerticalProfissionalService service = new VerticalProfissionalService(
                new CatalogoVerticalProfissionalAdapter(),
                new PermissaoAcessoService()
        );

        var vertical = service.detalharVertical(CodigoVerticalProfissional.BIOMED_PRO);

        assertThat(vertical).isPresent();
        assertThat(vertical.get().conselhoProfissional()).isEqualTo("CRBM");
        assertThat(vertical.get().capacidades()).contains("cadastro de habilitacoes", "rastreabilidade de lote e produto");
        assertThat(vertical.get().entidades()).contains("Habilitacao profissional", "Lote utilizado");
    }

    @Test
    void deveDetalharFisioProComCrefitoEPlanoTerapeutico() {
        VerticalProfissionalService service = new VerticalProfissionalService(
                new CatalogoVerticalProfissionalAdapter(),
                new PermissaoAcessoService()
        );

        var vertical = service.detalharVertical(CodigoVerticalProfissional.FISIO_PRO);

        assertThat(vertical).isPresent();
        assertThat(vertical.get().conselhoProfissional()).isEqualTo("CREFITO");
        assertThat(vertical.get().capacidades()).contains("avaliacao funcional", "plano terapeutico", "evolucao por sessao");
        assertThat(vertical.get().documentos()).contains("Relatorio fisioterapeutico");
    }

    @Test
    void deveDetalharPsicoProComoFuturoComCrpEPrivacidade() {
        VerticalProfissionalService service = new VerticalProfissionalService(
                new CatalogoVerticalProfissionalAdapter(),
                new PermissaoAcessoService()
        );

        var vertical = service.detalharVertical(CodigoVerticalProfissional.PSICO_PRO);

        assertThat(vertical).isPresent();
        assertThat(vertical.get().status().name()).isEqualTo("PREPARADO_FUTURO");
        assertThat(vertical.get().conselhoProfissional()).isEqualTo("CRP");
        assertThat(vertical.get().capacidades()).contains("documentos psicologicos", "controle de sigilo");
    }
}
