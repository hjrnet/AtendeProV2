package br.com.atendepro.modules.documento.application.service;

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
import br.com.atendepro.modules.documento.application.command.CriarCarimboProfissionalCommand;
import br.com.atendepro.modules.documento.application.port.out.SalvarCarimboProfissionalPort;
import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class CarimboProfissionalServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCriarCarimboNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarCarimboFake salvarCarimboFake = new SalvarCarimboFake();
        CarimboProfissionalService service = service(salvarCarimboFake);

        var result = service.criarCarimbo(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.uf()).isEqualTo("SP");
        assertThat(result.conselho()).isEqualTo(ConselhoProfissional.CRN);
        assertThat(salvarCarimboFake.carimboSalvo.clinicaNome()).isEqualTo("Clinica Vital");
    }

    @Test
    void deveListarCarimbosDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        CarimboProfissional carimbo = carimbo();
        CarimboProfissionalService service = new CarimboProfissionalService(
                carimboSalvo -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, busca, conselho, uf, profissionalId, ativo) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("marina");
                    assertThat(conselho).isEqualTo(ConselhoProfissional.CRN);
                    assertThat(uf).isEqualTo("SP");
                    assertThat(ativo).isTrue();
                    return new ResultadoPaginado<>(List.of(carimbo), 1, paginacao.pagina(), paginacao.tamanho());
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarCarimbos(
                null,
                new Paginacao(0, 20),
                "marina",
                ConselhoProfissional.CRN,
                "sp",
                null,
                true
        );

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("profissionalNome").containsExactly("Dra. Marina");
    }

    @Test
    void deveExigirEmpresaParaUsuarioGlobal() {
        CarimboProfissionalService service = service(carimbo -> {
        });

        assertThatThrownBy(() -> service.criarCarimbo(command(null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa e obrigatoria para operar carimbos.");
    }

    @Test
    void naoDeveOperarCarimbosSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        CarimboProfissionalService service = service(carimbo -> {
        });

        assertThatThrownBy(() -> service.listarCarimbos(null, new Paginacao(0, 20), null, null, null, null, true))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private CarimboProfissionalService service(SalvarCarimboProfissionalPort salvarPort) {
        return new CarimboProfissionalService(
                salvarPort,
                id -> Optional.empty(),
                (empresaId, paginacao, busca, conselho, uf, profissionalId, ativo) ->
                        new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CriarCarimboProfissionalCommand command(UUID empresaId) {
        return new CriarCarimboProfissionalCommand(
                empresaId,
                UUID.randomUUID(),
                "Dra. Marina",
                ConselhoProfissional.CRN,
                "sp",
                "CRN-12345",
                "Dra. Marina CRN-12345",
                "Clinica Vital"
        );
    }

    private CarimboProfissional carimbo() {
        return CarimboProfissional.criar(
                EMPRESA_ID,
                UUID.randomUUID(),
                "Dra. Marina",
                ConselhoProfissional.CRN,
                "SP",
                "CRN-12345",
                "Dra. Marina CRN-12345",
                "Clinica Vital",
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarCarimboFake implements SalvarCarimboProfissionalPort {

        private CarimboProfissional carimboSalvo;

        @Override
        public void salvarCarimbo(CarimboProfissional carimbo) {
            this.carimboSalvo = carimbo;
        }
    }
}
