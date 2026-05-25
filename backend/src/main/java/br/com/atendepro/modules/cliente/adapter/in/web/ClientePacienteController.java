package br.com.atendepro.modules.cliente.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.cliente.application.port.in.BuscarClientePacienteUseCase;
import br.com.atendepro.modules.cliente.application.port.in.CadastrarClientePacienteUseCase;
import br.com.atendepro.modules.cliente.application.port.in.ListarClientesPacientesUseCase;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes-pacientes")
@Profile("!test")
public class ClientePacienteController {

    private final CadastrarClientePacienteUseCase cadastrarClientePacienteUseCase;
    private final BuscarClientePacienteUseCase buscarClientePacienteUseCase;
    private final ListarClientesPacientesUseCase listarClientesPacientesUseCase;

    public ClientePacienteController(
            CadastrarClientePacienteUseCase cadastrarClientePacienteUseCase,
            BuscarClientePacienteUseCase buscarClientePacienteUseCase,
            ListarClientesPacientesUseCase listarClientesPacientesUseCase
    ) {
        this.cadastrarClientePacienteUseCase = cadastrarClientePacienteUseCase;
        this.buscarClientePacienteUseCase = buscarClientePacienteUseCase;
        this.listarClientesPacientesUseCase = listarClientesPacientesUseCase;
    }

    @PostMapping
    public ResponseEntity<ClientePacienteResponse> cadastrarClientePaciente(
            @Valid @RequestBody CadastrarClientePacienteRequest request
    ) {
        ClientePacienteResponse response = ClientePacienteResponse.de(
                cadastrarClientePacienteUseCase.cadastrarClientePaciente(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/clientes-pacientes/" + response.id())).body(response);
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClientePacienteResponse> buscarClientePaciente(@PathVariable UUID clienteId) {
        return buscarClientePacienteUseCase.buscarClientePacientePorId(clienteId)
                .map(ClientePacienteResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ClientesPacientesPaginadosResponse> listarClientesPacientes(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) AreaCliente area,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(ClientesPacientesPaginadosResponse.de(
                listarClientesPacientesUseCase.listarClientesPacientes(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        area,
                        ativo
                )
        ));
    }
}
