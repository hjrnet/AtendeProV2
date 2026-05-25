package br.com.atendepro.modules.spaces.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.spaces.application.command.AgendarOcupacaoSpacesCommand;
import br.com.atendepro.modules.spaces.application.command.ConsultarDisponibilidadeSpacesCommand;
import br.com.atendepro.modules.spaces.application.port.out.SalvarOcupacaoSpacesPort;
import br.com.atendepro.modules.spaces.domain.model.OcupacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.PacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.RecursoSpaces;
import br.com.atendepro.modules.spaces.domain.model.StatusOcupacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class OcupacaoSpacesServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("a88f3c56-2cd6-4fd4-8f1f-d1ef1ce9f2a1");
    private static final UUID RECURSO_ID = UUID.fromString("fb2c0250-e698-42cd-a57c-7998e18be60f");
    private static final UUID PACOTE_ID = UUID.fromString("f5b41cae-d72e-4663-b14a-1061e0f4b8b1");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveAgendarOcupacaoSemConflito() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarOcupacaoFake salvarFake = new SalvarOcupacaoFake();
        OcupacaoSpacesService service = service(salvarFake, false);

        var result = service.agendarOcupacao(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.recursoId()).isEqualTo(RECURSO_ID);
        assertThat(result.pacoteId()).isEqualTo(PACOTE_ID);
        assertThat(result.status()).isEqualTo(StatusOcupacaoSpaces.RESERVADA);
        assertThat(salvarFake.ocupacaoSalva.nomeParceiro()).isEqualTo("Dra. Marina");
    }

    @Test
    void naoDeveAgendarQuandoRecursoEstaOcupado() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        OcupacaoSpacesService service = service(ocupacao -> {
        }, true);

        assertThatThrownBy(() -> service.agendarOcupacao(command(null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Recurso Spaces ja possui ocupacao no periodo informado.");
    }

    @Test
    void deveConsultarDisponibilidadeDoRecurso() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        OcupacaoSpacesService service = service(ocupacao -> {
        }, false);

        var result = service.consultarDisponibilidade(new ConsultarDisponibilidadeSpacesCommand(
                null,
                RECURSO_ID,
                Instant.parse("2026-05-26T12:00:00Z"),
                Instant.parse("2026-05-26T14:00:00Z")
        ));

        assertThat(result.disponivel()).isTrue();
        assertThat(result.motivo()).isEqualTo("Recurso disponivel no periodo informado.");
    }

    @Test
    void deveListarOcupacoesDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        OcupacaoSpaces ocupacao = ocupacao();
        OcupacaoSpacesService service = new OcupacaoSpacesService(
                ocupacaoSalva -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, recursoId, inicioEm, fimEm, status) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(recursoId).isEqualTo(RECURSO_ID);
                    assertThat(status).isEqualTo(StatusOcupacaoSpaces.RESERVADA);
                    return new ResultadoPaginado<>(List.of(ocupacao), 1, paginacao.pagina(), paginacao.tamanho());
                },
                (empresaId, recursoId, inicioEm, fimEm) -> false,
                id -> Optional.of(recurso()),
                id -> Optional.of(pacote()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarOcupacoes(null, new Paginacao(0, 20), RECURSO_ID, null, null, StatusOcupacaoSpaces.RESERVADA);

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("nomeParceiro").containsExactly("Dra. Marina");
    }

    private OcupacaoSpacesService service(SalvarOcupacaoSpacesPort salvarPort, boolean existeConflito) {
        return new OcupacaoSpacesService(
                salvarPort,
                id -> Optional.empty(),
                (empresaId, paginacao, recursoId, inicioEm, fimEm, status) -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                (empresaId, recursoId, inicioEm, fimEm) -> existeConflito,
                id -> Optional.of(recurso()),
                id -> Optional.of(pacote()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private AgendarOcupacaoSpacesCommand command(UUID empresaId) {
        return new AgendarOcupacaoSpacesCommand(
                empresaId,
                RECURSO_ID,
                PACOTE_ID,
                "Dra. Marina",
                Instant.parse("2026-05-26T12:00:00Z"),
                Instant.parse("2026-05-26T14:00:00Z"),
                null,
                "Sublocacao de teste"
        );
    }

    private OcupacaoSpaces ocupacao() {
        return OcupacaoSpaces.agendar(
                EMPRESA_ID,
                RECURSO_ID,
                PACOTE_ID,
                "Dra. Marina",
                Instant.parse("2026-05-26T12:00:00Z"),
                Instant.parse("2026-05-26T14:00:00Z"),
                StatusOcupacaoSpaces.RESERVADA,
                "Sublocacao de teste",
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private RecursoSpaces recurso() {
        return RecursoSpaces.cadastrar(
                EMPRESA_ID,
                "Sala premium",
                TipoRecursoSpaces.SALA,
                "Sala com maca",
                2,
                "Unidade Paulista",
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private PacoteSublocacaoSpaces pacote() {
        return PacoteSublocacaoSpaces.cadastrar(
                EMPRESA_ID,
                RECURSO_ID,
                "Hora avulsa",
                TipoPacoteSublocacaoSpaces.HORA,
                "Uso avulso da sala",
                new BigDecimal("1.00"),
                new BigDecimal("80.00"),
                BigDecimal.ZERO,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarOcupacaoFake implements SalvarOcupacaoSpacesPort {

        private OcupacaoSpaces ocupacaoSalva;

        @Override
        public void salvarOcupacao(OcupacaoSpaces ocupacao) {
            this.ocupacaoSalva = ocupacao;
        }
    }
}
