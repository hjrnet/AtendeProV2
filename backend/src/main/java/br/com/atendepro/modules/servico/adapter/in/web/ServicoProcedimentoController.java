package br.com.atendepro.modules.servico.adapter.in.web;

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

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.servico.application.port.in.BuscarServicoProcedimentoUseCase;
import br.com.atendepro.modules.servico.application.port.in.CadastrarServicoProcedimentoUseCase;
import br.com.atendepro.modules.servico.application.port.in.ListarServicosProcedimentosUseCase;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servicos-procedimentos")
@Profile("!test")
public class ServicoProcedimentoController {

    private final CadastrarServicoProcedimentoUseCase cadastrarServicoProcedimentoUseCase;
    private final BuscarServicoProcedimentoUseCase buscarServicoProcedimentoUseCase;
    private final ListarServicosProcedimentosUseCase listarServicosProcedimentosUseCase;

    public ServicoProcedimentoController(
            CadastrarServicoProcedimentoUseCase cadastrarServicoProcedimentoUseCase,
            BuscarServicoProcedimentoUseCase buscarServicoProcedimentoUseCase,
            ListarServicosProcedimentosUseCase listarServicosProcedimentosUseCase
    ) {
        this.cadastrarServicoProcedimentoUseCase = cadastrarServicoProcedimentoUseCase;
        this.buscarServicoProcedimentoUseCase = buscarServicoProcedimentoUseCase;
        this.listarServicosProcedimentosUseCase = listarServicosProcedimentosUseCase;
    }

    @PostMapping
    public ResponseEntity<ServicoProcedimentoResponse> cadastrarServicoProcedimento(
            @Valid @RequestBody CadastrarServicoProcedimentoRequest request
    ) {
        ServicoProcedimentoResponse response = ServicoProcedimentoResponse.de(
                cadastrarServicoProcedimentoUseCase.cadastrarServicoProcedimento(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/servicos-procedimentos/" + response.id())).body(response);
    }

    @GetMapping("/{servicoId}")
    public ResponseEntity<ServicoProcedimentoResponse> buscarServicoProcedimento(@PathVariable UUID servicoId) {
        return buscarServicoProcedimentoUseCase.buscarServicoProcedimentoPorId(servicoId)
                .map(ServicoProcedimentoResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ServicosProcedimentosPaginadosResponse> listarServicosProcedimentos(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) AreaCliente area,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(ServicosProcedimentosPaginadosResponse.de(
                listarServicosProcedimentosUseCase.listarServicosProcedimentos(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        area,
                        ativo
                )
        ));
    }
}
