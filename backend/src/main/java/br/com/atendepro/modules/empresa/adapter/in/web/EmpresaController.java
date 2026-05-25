package br.com.atendepro.modules.empresa.adapter.in.web;

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

import br.com.atendepro.modules.empresa.application.port.in.BuscarEmpresaUseCase;
import br.com.atendepro.modules.empresa.application.port.in.CadastrarEmpresaUseCase;
import br.com.atendepro.modules.empresa.application.port.in.ListarEmpresasUseCase;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/empresas")
@Profile("!test")
public class EmpresaController {

    private final CadastrarEmpresaUseCase cadastrarEmpresaUseCase;
    private final BuscarEmpresaUseCase buscarEmpresaUseCase;
    private final ListarEmpresasUseCase listarEmpresasUseCase;

    public EmpresaController(
            CadastrarEmpresaUseCase cadastrarEmpresaUseCase,
            BuscarEmpresaUseCase buscarEmpresaUseCase,
            ListarEmpresasUseCase listarEmpresasUseCase
    ) {
        this.cadastrarEmpresaUseCase = cadastrarEmpresaUseCase;
        this.buscarEmpresaUseCase = buscarEmpresaUseCase;
        this.listarEmpresasUseCase = listarEmpresasUseCase;
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> cadastrarEmpresa(@Valid @RequestBody CadastrarEmpresaRequest request) {
        EmpresaResponse response = EmpresaResponse.de(cadastrarEmpresaUseCase.cadastrarEmpresa(request.paraCommand()));
        return ResponseEntity.created(URI.create("/api/empresas/" + response.id())).body(response);
    }

    @GetMapping("/{empresaId}")
    public ResponseEntity<EmpresaResponse> buscarEmpresa(@PathVariable UUID empresaId) {
        return buscarEmpresaUseCase.buscarEmpresaPorId(empresaId)
                .map(EmpresaResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<EmpresasPaginadasResponse> listarEmpresas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return ResponseEntity.ok(EmpresasPaginadasResponse.de(
                listarEmpresasUseCase.listarEmpresas(new Paginacao(pagina, tamanho))
        ));
    }
}
