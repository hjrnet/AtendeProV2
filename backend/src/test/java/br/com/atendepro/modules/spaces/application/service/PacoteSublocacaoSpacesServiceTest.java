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
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.spaces.application.command.CadastrarPacoteSublocacaoSpacesCommand;
import br.com.atendepro.modules.spaces.application.port.out.SalvarPacoteSublocacaoSpacesPort;
import br.com.atendepro.modules.spaces.domain.model.PacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.RecursoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

class PacoteSublocacaoSpacesServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("f1cf1b1b-3cf0-4a1d-8e32-c451408bdbd6");
    private static final UUID RECURSO_ID = UUID.fromString("d10610e1-99f7-4c79-9e85-729115c9cf43");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCadastrarPacoteNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarPacoteFake salvarPacoteFake = new SalvarPacoteFake();
        PacoteSublocacaoSpacesService service = service(salvarPacoteFake);

        var result = service.cadastrarPacote(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.recursoId()).isEqualTo(RECURSO_ID);
        assertThat(result.tipo()).isEqualTo(TipoPacoteSublocacaoSpaces.HORA);
        assertThat(salvarPacoteFake.pacoteSalvo.valorFixo()).isEqualByComparingTo("80.00");
    }

    @Test
    void deveListarPacotesDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        PacoteSublocacaoSpaces pacote = pacote();
        PacoteSublocacaoSpacesService service = new PacoteSublocacaoSpacesService(
                pacoteSalvo -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, busca, recursoId, tipo, ativo) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("hora");
                    assertThat(recursoId).isEqualTo(RECURSO_ID);
                    assertThat(tipo).isEqualTo(TipoPacoteSublocacaoSpaces.HORA);
                    return new ResultadoPaginado<>(List.of(pacote), 1, paginacao.pagina(), paginacao.tamanho());
                },
                id -> Optional.of(recurso()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarPacotes(null, new Paginacao(0, 20), "hora", RECURSO_ID, TipoPacoteSublocacaoSpaces.HORA, true);

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("nome").containsExactly("Hora avulsa");
    }

    @Test
    void naoDeveOperarPacotesSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        PacoteSublocacaoSpacesService service = service(pacote -> {
        });

        assertThatThrownBy(() -> service.listarPacotes(null, new Paginacao(0, 20), null, null, null, true))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private PacoteSublocacaoSpacesService service(SalvarPacoteSublocacaoSpacesPort salvarPort) {
        return new PacoteSublocacaoSpacesService(
                salvarPort,
                id -> Optional.empty(),
                (empresaId, paginacao, busca, recursoId, tipo, ativo) -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                id -> Optional.of(recurso()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CadastrarPacoteSublocacaoSpacesCommand command(UUID empresaId) {
        return new CadastrarPacoteSublocacaoSpacesCommand(
                empresaId,
                RECURSO_ID,
                "Hora avulsa",
                TipoPacoteSublocacaoSpaces.HORA,
                "Uso avulso da sala",
                new BigDecimal("1.00"),
                new BigDecimal("80.00"),
                BigDecimal.ZERO
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

    private static class SalvarPacoteFake implements SalvarPacoteSublocacaoSpacesPort {

        private PacoteSublocacaoSpaces pacoteSalvo;

        @Override
        public void salvarPacote(PacoteSublocacaoSpaces pacote) {
            this.pacoteSalvo = pacote;
        }
    }
}
