package br.com.atendepro.modules.empresa.application.service;

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
import br.com.atendepro.modules.empresa.application.command.CadastrarEmpresaCommand;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.empresa.application.result.EmpresaResult;
import br.com.atendepro.modules.empresa.domain.model.DocumentoEmpresa;
import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class EmpresaServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCadastrarEmpresa() {
        SalvarEmpresaFake salvarEmpresaFake = new SalvarEmpresaFake();
        EmpresaService service = new EmpresaService(
                documento -> Optional.empty(),
                id -> Optional.empty(),
                paginacao -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                salvarEmpresaFake,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.cadastrarEmpresa(command());

        assertThat(result.nomeFantasia()).isEqualTo("Clinica AtendePro");
        assertThat(result.documento()).isEqualTo("12345678000190");
        assertThat(salvarEmpresaFake.empresaSalva.ativo()).isTrue();
    }

    @Test
    void naoDeveCadastrarDocumentoDuplicado() {
        EmpresaService service = new EmpresaService(
                documento -> Optional.of(empresa()),
                id -> Optional.empty(),
                paginacao -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                empresa -> {
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        assertThatThrownBy(() -> service.cadastrarEmpresa(command()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Documento de empresa ja cadastrado.");
    }

    @Test
    void deveListarEmpresasPaginadas() {
        EmpresaService service = new EmpresaService(
                documento -> Optional.empty(),
                id -> Optional.empty(),
                paginacao -> new ResultadoPaginado<>(List.of(empresa()), 1, paginacao.pagina(), paginacao.tamanho()),
                empresa -> {
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarEmpresas(new Paginacao(0, 20));

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).hasSize(1);
    }

    @Test
    void deveListarSomenteEmpresaDoContextoRestrito() {
        TenantContextHolder.definir(new TenantContext(
                UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf"),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.EMPRESA_ADMIN)
        ));
        EmpresaService service = new EmpresaService(
                documento -> Optional.empty(),
                id -> Optional.of(empresa()),
                paginacao -> {
                    throw new AssertionError("Listagem global nao deve ser usada para tenant restrito.");
                },
                empresa -> {
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarEmpresas(new Paginacao(0, 20));

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting(EmpresaResult::id)
                .containsExactly(UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf"));
    }

    @Test
    void naoDeveCadastrarEmpresaComPerfilSemPermissaoGlobal() {
        TenantContextHolder.definir(new TenantContext(
                UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf"),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.PROFISSIONAL)
        ));
        EmpresaService service = new EmpresaService(
                documento -> Optional.empty(),
                id -> Optional.empty(),
                paginacao -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                empresa -> {
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        assertThatThrownBy(() -> service.cadastrarEmpresa(command()))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private CadastrarEmpresaCommand command() {
        return new CadastrarEmpresaCommand(
                "Clinica AtendePro",
                "Clinica AtendePro LTDA",
                DocumentoEmpresa.de("12.345.678/0001-90"),
                "contato@clinica.test",
                "11999999999"
        );
    }

    private EmpresaTenant empresa() {
        return new EmpresaTenant(
                UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf"),
                "Clinica AtendePro",
                "Clinica AtendePro LTDA",
                DocumentoEmpresa.de("12345678000190"),
                "contato@clinica.test",
                "11999999999",
                true,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarEmpresaFake implements br.com.atendepro.modules.empresa.application.port.out.SalvarEmpresaPort {

        private EmpresaTenant empresaSalva;

        @Override
        public void salvarEmpresa(EmpresaTenant empresa) {
            this.empresaSalva = empresa;
        }
    }
}
