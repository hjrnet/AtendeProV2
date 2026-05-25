package br.com.atendepro.modules.nutri.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.nutri.application.command.CriarAvaliacaoAntropometricaNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.DetalharAvaliacaoAntropometricaNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarProntuarioNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarVisaoNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarAvaliacoesAntropometricasNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarPacientesNutriProCommand;
import br.com.atendepro.modules.nutri.domain.model.AvaliacaoAntropometricaNutriPro;
import br.com.atendepro.modules.nutri.domain.model.ObjetivoNutricionalNutriPro;
import br.com.atendepro.modules.nutri.domain.model.SexoBiologicoNutriPro;
import br.com.atendepro.modules.nutri.application.result.DadosProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.MetricasNutriProResult;
import br.com.atendepro.modules.nutri.application.result.PacienteProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.application.result.PacienteNutriResumoResult;
import br.com.atendepro.modules.nutri.application.result.ResumoProntuarioNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.StatusOperacionalNutriPro;
import br.com.atendepro.shared.domain.exception.BusinessException;

class NutriProServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");
    private static final UUID OUTRA_EMPRESA_ID = UUID.fromString("f899c2f6-2bc1-4e08-b730-686c42e07850");
    private static final UUID PACIENTE_ID = UUID.fromString("1f9c1ef0-f3c8-4564-98ad-6c2fd1d20f24");
    private static final UUID AVALIACAO_ID = UUID.fromString("61a49036-298c-42a4-98e6-f2f061e1a43c");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T10:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveConsultarVisaoNutriProNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        NutriProService service = service(metricasComDados());

        var result = service.consultarVisaoNutriPro(new ConsultarVisaoNutriProCommand(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.statusOperacional()).isEqualTo(StatusOperacionalNutriPro.OPERACIONAL);
        assertThat(result.indicadores()).extracting("codigo").contains("pacientes", "precificacao", "planos");
        assertThat(result.atalhosPrioritarios()).hasSize(3);
    }

    @Test
    void deveListarPacientesNutriProNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        NutriProService service = service(metricasComDados());

        var result = service.listarPacientesNutriPro(new ListarPacientesNutriProCommand(null, "ana"));

        assertThat(result).extracting("nome").containsExactly("Ana Nutri");
    }

    @Test
    void deveConsultarProntuarioNutricionalDoPaciente() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        NutriProService service = service(metricasComDados());

        var result = service.consultarProntuarioNutriPro(new ConsultarProntuarioNutriProCommand(null, PACIENTE_ID));

        assertThat(result).isPresent();
        assertThat(result.get().paciente().nome()).isEqualTo("Ana Nutri");
        assertThat(result.get().acoesRapidas()).extracting("codigo")
                .contains("gasto-energetico", "exames-laboratoriais", "plano-alimentar");
    }

    @Test
    void deveCriarAvaliacaoAntropometricaComImcEGastoEnergetico() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        var avaliacaoSalva = new AtomicReference<AvaliacaoAntropometricaNutriPro>();
        NutriProService service = service(metricasComDados(), avaliacaoSalva, true);

        var result = service.criarAvaliacaoAntropometrica(new CriarAvaliacaoAntropometricaNutriProCommand(
                null,
                PACIENTE_ID,
                new BigDecimal("70.00"),
                new BigDecimal("170.00"),
                30,
                SexoBiologicoNutriPro.FEMININO,
                ObjetivoNutricionalNutriPro.PERDA_DE_PESO,
                new BigDecimal("1.40"),
                "Meta inicial para plano alimentar."
        ));

        assertThat(avaliacaoSalva.get()).isNotNull();
        assertThat(result.imc()).isEqualByComparingTo("24.22");
        assertThat(result.gebKcal()).isEqualByComparingTo("1451.50");
        assertThat(result.tmbKcal()).isEqualByComparingTo("1451.50");
        assertThat(result.getKcal()).isEqualByComparingTo("2032.10");
        assertThat(result.metaEnergeticaKcal()).isEqualByComparingTo("1632.10");
        assertThat(result.formula()).contains("Mifflin");
        assertThat(result.aviso()).contains("estimativos");
    }

    @Test
    void deveListarEDetalharAvaliacoesAntropometricasDoPaciente() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        NutriProService service = service(metricasComDados());
        UUID avaliacaoId = AVALIACAO_ID;

        var lista = service.listarAvaliacoesAntropometricas(new ListarAvaliacoesAntropometricasNutriProCommand(null, PACIENTE_ID));
        var detalhe = service.detalharAvaliacaoAntropometrica(new DetalharAvaliacaoAntropometricaNutriProCommand(null, PACIENTE_ID, avaliacaoId));

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).id()).isEqualTo(avaliacaoId);
        assertThat(detalhe).isPresent();
        assertThat(detalhe.get().objetivo()).isEqualTo(ObjetivoNutricionalNutriPro.MANUTENCAO);
    }

    @Test
    void naoDeveCriarAvaliacaoParaPacienteNutriInexistente() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        NutriProService service = service(metricasComDados(), new AtomicReference<>(), false);

        assertThatThrownBy(() -> service.criarAvaliacaoAntropometrica(new CriarAvaliacaoAntropometricaNutriProCommand(
                null,
                PACIENTE_ID,
                new BigDecimal("70.00"),
                new BigDecimal("170.00"),
                30,
                SexoBiologicoNutriPro.FEMININO,
                ObjetivoNutricionalNutriPro.MANUTENCAO,
                new BigDecimal("1.40"),
                null
        )))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Paciente de nutricao nao encontrado nesta empresa.");
    }

    @Test
    void deveExigirEmpresaParaUsuarioGlobal() {
        NutriProService service = service(metricasComDados());

        assertThatThrownBy(() -> service.consultarVisaoNutriPro(new ConsultarVisaoNutriProCommand(null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa e obrigatoria para operar Nutri Pro.");
    }

    @Test
    void naoDevePermitirOutraEmpresaParaTenantRestrito() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        NutriProService service = service(metricasComDados());

        assertThatThrownBy(() -> service.consultarVisaoNutriPro(new ConsultarVisaoNutriProCommand(OUTRA_EMPRESA_ID)))
                .hasMessage("Usuario nao possui acesso a esta empresa.");
    }

    @Test
    void naoDeveConsultarNutriProSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        NutriProService service = service(metricasComDados());

        assertThatThrownBy(() -> service.consultarVisaoNutriPro(new ConsultarVisaoNutriProCommand(null)))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private NutriProService service(MetricasNutriProResult metricas) {
        return service(metricas, new AtomicReference<>(), true);
    }

    private NutriProService service(
            MetricasNutriProResult metricas,
            AtomicReference<AvaliacaoAntropometricaNutriPro> avaliacaoSalva,
            boolean pacienteExiste
    ) {
        return new NutriProService(
                (empresaId, hoje) -> {
                    assertThat(hoje).isEqualTo(LocalDate.parse("2026-05-25"));
                    return metricas;
                },
                (empresaId, busca) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("ana");
                    return List.of(pacienteResumo());
                },
                (empresaId, pacienteId, hoje) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(pacienteId).isEqualTo(PACIENTE_ID);
                    assertThat(hoje).isEqualTo(LocalDate.parse("2026-05-25"));
                    return Optional.of(prontuarioDados());
                },
                (empresaId, pacienteId) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(pacienteId).isEqualTo(PACIENTE_ID);
                    return pacienteExiste;
                },
                avaliacaoSalva::set,
                (empresaId, pacienteId) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(pacienteId).isEqualTo(PACIENTE_ID);
                    return List.of(avaliacaoExistente());
                },
                (empresaId, pacienteId, avaliacaoId) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(pacienteId).isEqualTo(PACIENTE_ID);
                    return Optional.of(avaliacaoExistente());
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private MetricasNutriProResult metricasComDados() {
        return new MetricasNutriProResult(
                "Clinica Nutri Vida",
                5,
                1,
                3,
                4,
                2,
                3,
                1,
                0,
                List.of()
        );
    }

    private PacienteNutriResumoResult pacienteResumo() {
        return new PacienteNutriResumoResult(
                PACIENTE_ID,
                "Ana Nutri",
                "21999999999",
                "Acompanhamento nutricional",
                true,
                Instant.parse("2026-05-25T09:00:00Z")
        );
    }

    private DadosProntuarioNutriProResult prontuarioDados() {
        return new DadosProntuarioNutriProResult(
                new PacienteProntuarioNutriProResult(
                        PACIENTE_ID,
                        EMPRESA_ID,
                        "Ana Nutri",
                        "ana.nutri@test.local",
                        "21999999999",
                        LocalDate.parse("1992-03-10"),
                        34,
                        "Paciente com foco em performance.",
                        true,
                        Instant.parse("2026-05-25T09:00:00Z")
                ),
                new ResumoProntuarioNutriProResult(
                        2,
                        1,
                        3,
                        0,
                        "PREPARADO",
                        "PREPARADO",
                        "PROXIMA_TASK",
                        "PROXIMA_TASK",
                        "PREPARADO",
                        Instant.parse("2026-05-20T12:00:00Z")
                )
        );
    }

    private AvaliacaoAntropometricaNutriPro avaliacaoExistente() {
        AvaliacaoAntropometricaNutriPro calculada = AvaliacaoAntropometricaNutriPro.registrar(
                EMPRESA_ID,
                PACIENTE_ID,
                new BigDecimal("68.00"),
                new BigDecimal("168.00"),
                34,
                SexoBiologicoNutriPro.FEMININO,
                ObjetivoNutricionalNutriPro.MANUTENCAO,
                new BigDecimal("1.40"),
                "Historico inicial.",
                Instant.parse("2026-05-25T09:00:00Z")
        );
        return new AvaliacaoAntropometricaNutriPro(
                AVALIACAO_ID,
                calculada.empresaId(),
                calculada.pacienteId(),
                calculada.pesoKg(),
                calculada.alturaCm(),
                calculada.idade(),
                calculada.sexo(),
                calculada.imc(),
                calculada.objetivo(),
                calculada.fatorAtividade(),
                calculada.gebKcal(),
                calculada.tmbKcal(),
                calculada.getKcal(),
                calculada.metaEnergeticaKcal(),
                calculada.formula(),
                calculada.aviso(),
                calculada.observacoes(),
                calculada.criadoEm(),
                calculada.atualizadoEm()
        );
    }
}
