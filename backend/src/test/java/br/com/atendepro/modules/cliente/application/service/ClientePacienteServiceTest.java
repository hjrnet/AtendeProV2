package br.com.atendepro.modules.cliente.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
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
import br.com.atendepro.modules.cliente.application.command.CadastrarClientePacienteCommand;
import br.com.atendepro.modules.cliente.application.port.out.SalvarClientePacientePort;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.cliente.domain.model.ClientePaciente;
import br.com.atendepro.modules.cliente.domain.model.TipoCliente;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class ClientePacienteServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");
    private static final UUID OUTRA_EMPRESA_ID = UUID.fromString("f899c2f6-2bc1-4e08-b730-686c42e07850");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCadastrarClientePacienteNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarClienteFake salvarClienteFake = new SalvarClienteFake();
        ClientePacienteService service = service(salvarClienteFake, Optional.empty());

        var result = service.cadastrarClientePaciente(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.nome()).isEqualTo("Ana Cliente");
        assertThat(salvarClienteFake.clienteSalvo.empresaId()).isEqualTo(EMPRESA_ID);
    }

    @Test
    void deveListarClientesDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        ClientePaciente cliente = cliente(EMPRESA_ID);
        ClientePacienteService service = new ClientePacienteService(
                clienteSalvo -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, busca, area, ativo) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("ana");
                    assertThat(area).isEqualTo(AreaCliente.NUTRI);
                    return new ResultadoPaginado<>(List.of(cliente), 1, paginacao.pagina(), paginacao.tamanho());
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarClientesPacientes(null, new Paginacao(0, 20), "ana", AreaCliente.NUTRI, true);

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("nome").containsExactly("Ana Cliente");
    }

    @Test
    void deveExigirEmpresaParaUsuarioGlobal() {
        ClientePacienteService service = service(cliente -> {
        }, Optional.empty());

        assertThatThrownBy(() -> service.cadastrarClientePaciente(command(null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa e obrigatoria para operar clientes.");
    }

    @Test
    void naoDevePermitirOutraEmpresaParaTenantRestrito() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        ClientePacienteService service = service(cliente -> {
        }, Optional.empty());

        assertThatThrownBy(() -> service.cadastrarClientePaciente(command(OUTRA_EMPRESA_ID)))
                .hasMessage("Usuario nao possui acesso a esta empresa.");
    }

    @Test
    void naoDeveOperarClientesSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        ClientePacienteService service = service(cliente -> {
        }, Optional.empty());

        assertThatThrownBy(() -> service.listarClientesPacientes(null, new Paginacao(0, 20), null, null, true))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private ClientePacienteService service(SalvarClientePacientePort salvarPort, Optional<ClientePaciente> cliente) {
        return new ClientePacienteService(
                salvarPort,
                id -> cliente,
                (empresaId, paginacao, busca, area, ativo) -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CadastrarClientePacienteCommand command(UUID empresaId) {
        return new CadastrarClientePacienteCommand(
                empresaId,
                "Ana Cliente",
                TipoCliente.CLIENTE_PACIENTE,
                AreaCliente.NUTRI,
                "123.456.789-00",
                "ana@test.local",
                "11999999999",
                LocalDate.parse("1990-01-10"),
                "Cliente de teste"
        );
    }

    private ClientePaciente cliente(UUID empresaId) {
        return ClientePaciente.cadastrar(
                empresaId,
                "Ana Cliente",
                TipoCliente.CLIENTE_PACIENTE,
                AreaCliente.NUTRI,
                "12345678900",
                "ana@test.local",
                "11999999999",
                LocalDate.parse("1990-01-10"),
                null,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarClienteFake implements SalvarClientePacientePort {

        private ClientePaciente clienteSalvo;

        @Override
        public void salvarClientePaciente(ClientePaciente cliente) {
            this.clienteSalvo = cliente;
        }
    }
}
