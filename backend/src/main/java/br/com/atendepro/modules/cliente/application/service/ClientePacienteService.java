package br.com.atendepro.modules.cliente.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.cliente.application.command.CadastrarClientePacienteCommand;
import br.com.atendepro.modules.cliente.application.port.in.BuscarClientePacienteUseCase;
import br.com.atendepro.modules.cliente.application.port.in.CadastrarClientePacienteUseCase;
import br.com.atendepro.modules.cliente.application.port.in.ListarClientesPacientesUseCase;
import br.com.atendepro.modules.cliente.application.port.out.CarregarClientePacientePorIdPort;
import br.com.atendepro.modules.cliente.application.port.out.ListarClientesPacientesPort;
import br.com.atendepro.modules.cliente.application.port.out.SalvarClientePacientePort;
import br.com.atendepro.modules.cliente.application.result.ClientePacienteResult;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.cliente.domain.model.ClientePaciente;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class ClientePacienteService implements
        CadastrarClientePacienteUseCase,
        BuscarClientePacienteUseCase,
        ListarClientesPacientesUseCase {

    private final SalvarClientePacientePort salvarClientePacientePort;
    private final CarregarClientePacientePorIdPort carregarClientePacientePorIdPort;
    private final ListarClientesPacientesPort listarClientesPacientesPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public ClientePacienteService(
            SalvarClientePacientePort salvarClientePacientePort,
            CarregarClientePacientePorIdPort carregarClientePacientePorIdPort,
            ListarClientesPacientesPort listarClientesPacientesPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarClientePacientePort = salvarClientePacientePort;
        this.carregarClientePacientePorIdPort = carregarClientePacientePorIdPort;
        this.listarClientesPacientesPort = listarClientesPacientesPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public ClientePacienteResult cadastrarClientePaciente(CadastrarClientePacienteCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        ClientePaciente cliente = ClientePaciente.cadastrar(
                empresaId,
                command.nome(),
                command.tipo(),
                command.area(),
                command.documento(),
                command.email(),
                command.telefone(),
                command.dataNascimento(),
                command.observacoes(),
                Instant.now(clock)
        );
        salvarClientePacientePort.salvarClientePaciente(cliente);
        return ClientePacienteResult.de(cliente);
    }

    @Override
    public Optional<ClientePacienteResult> buscarClientePacientePorId(UUID clienteId) {
        validarPermissao();
        return carregarClientePacientePorIdPort.carregarClientePacientePorId(clienteId)
                .filter(cliente -> {
                    tenantAccessService.validarAcessoEmpresa(cliente.empresaId());
                    return true;
                })
                .map(ClientePacienteResult::de);
    }

    @Override
    public ResultadoPaginado<ClientePacienteResult> listarClientesPacientes(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            AreaCliente area,
            Boolean ativo
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        ResultadoPaginado<ClientePaciente> clientes = listarClientesPacientesPort.listarClientesPacientes(
                empresaResolvida,
                paginacao,
                busca,
                area,
                ativo
        );
        return new ResultadoPaginado<>(
                clientes.itens().stream().map(ClientePacienteResult::de).toList(),
                clientes.totalItens(),
                clientes.pagina(),
                clientes.tamanho()
        );
    }

    private UUID resolverEmpresaId(UUID empresaIdSolicitada) {
        Optional<UUID> empresaRestrita = tenantAccessService.empresaRestrita();
        if (empresaRestrita.isPresent()) {
            if (empresaIdSolicitada != null) {
                tenantAccessService.validarAcessoEmpresa(empresaIdSolicitada);
            }
            return empresaRestrita.get();
        }
        if (empresaIdSolicitada == null) {
            throw new BusinessException("CLIENTE_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar clientes.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_CLIENTES);
    }
}
