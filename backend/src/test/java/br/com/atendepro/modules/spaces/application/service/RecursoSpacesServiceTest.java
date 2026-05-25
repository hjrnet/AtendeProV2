package br.com.atendepro.modules.spaces.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import br.com.atendepro.modules.spaces.application.command.CadastrarRecursoSpacesCommand;
import br.com.atendepro.modules.spaces.application.port.out.SalvarRecursoSpacesPort;
import br.com.atendepro.modules.spaces.domain.model.RecursoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

class RecursoSpacesServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCadastrarRecursoNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarRecursoFake salvarRecursoFake = new SalvarRecursoFake();
        RecursoSpacesService service = service(salvarRecursoFake);

        var result = service.cadastrarRecurso(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.nome()).isEqualTo("Sala premium");
        assertThat(result.tipo()).isEqualTo(TipoRecursoSpaces.SALA);
        assertThat(salvarRecursoFake.recursoSalvo.capacidadePessoas()).isEqualTo(2);
    }

    @Test
    void deveListarRecursosDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        RecursoSpaces recurso = recurso();
        RecursoSpacesService service = new RecursoSpacesService(
                recursoSalvo -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, busca, tipo, ativo) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("premium");
                    assertThat(tipo).isEqualTo(TipoRecursoSpaces.SALA);
                    assertThat(ativo).isTrue();
                    return new ResultadoPaginado<>(List.of(recurso), 1, paginacao.pagina(), paginacao.tamanho());
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarRecursos(null, new Paginacao(0, 20), "premium", TipoRecursoSpaces.SALA, true);

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("nome").containsExactly("Sala premium");
    }

    @Test
    void naoDeveOperarRecursosSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        RecursoSpacesService service = service(recurso -> {
        });

        assertThatThrownBy(() -> service.listarRecursos(null, new Paginacao(0, 20), null, null, true))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private RecursoSpacesService service(SalvarRecursoSpacesPort salvarPort) {
        return new RecursoSpacesService(
                salvarPort,
                id -> Optional.empty(),
                (empresaId, paginacao, busca, tipo, ativo) -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CadastrarRecursoSpacesCommand command(UUID empresaId) {
        return new CadastrarRecursoSpacesCommand(
                empresaId,
                "Sala premium",
                TipoRecursoSpaces.SALA,
                "Sala com maca",
                2,
                "Unidade Paulista"
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

    private static class SalvarRecursoFake implements SalvarRecursoSpacesPort {

        private RecursoSpaces recursoSalvo;

        @Override
        public void salvarRecurso(RecursoSpaces recurso) {
            this.recursoSalvo = recurso;
        }
    }
}
