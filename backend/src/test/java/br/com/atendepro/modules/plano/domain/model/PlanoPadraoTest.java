package br.com.atendepro.modules.plano.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class PlanoPadraoTest {

    @Test
    void deveDefinirOsDezPlanosPadraoDaR2() {
        assertThat(Arrays.stream(PlanoPadrao.values()).map(PlanoPadrao::codigo))
                .containsExactly(
                        "ESTUDANTE",
                        "START",
                        "CARE",
                        "NUTRI_PRO",
                        "BEAUTY_PRO",
                        "BIOMED_PRO",
                        "FISIO_PRO",
                        "BUSINESS",
                        "SPACES",
                        "PREMIUM"
                );
    }

    @Test
    void todosOsPlanosPadraoDevemTerModuloComumELimitesValidos() {
        assertThat(PlanoPadrao.values()).allSatisfy(plano -> {
            assertThat(plano.valorMensal()).isNotNegative();
            assertThat(plano.limiteUsuarios()).isGreaterThan(0);
            assertThat(plano.limiteClientes()).isGreaterThan(0);
            assertThat(plano.limiteProfissionais()).isGreaterThan(0);
            assertThat(plano.modulos())
                    .contains(ModuloPlano.TENANT_EMPRESA, ModuloPlano.USUARIOS_PERMISSOES, ModuloPlano.DASHBOARD);
        });
    }

    @Test
    void planoEstudanteDeveTerMarcaDaguaAcademicaELimitesReduzidos() {
        assertThat(PlanoPadrao.ESTUDANTE.estudante()).isTrue();
        assertThat(PlanoPadrao.ESTUDANTE.marcaDaguaAcademica()).contains("Uso academico");
        assertThat(PlanoPadrao.ESTUDANTE.limiteUsuarios()).isEqualTo(1);
        assertThat(PlanoPadrao.ESTUDANTE.limiteClientes()).isEqualTo(30);
        assertThat(PlanoPadrao.ESTUDANTE.limiteProfissionais()).isEqualTo(1);
    }
}
