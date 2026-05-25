package br.com.atendepro.modules.busca.adapter.in.web;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.busca.application.port.in.BuscarGlobalUseCase;

@RestController
@RequestMapping("/api/busca/global")
@Profile("!test")
public class BuscaGlobalController {

    private final BuscarGlobalUseCase buscarGlobalUseCase;

    public BuscaGlobalController(BuscarGlobalUseCase buscarGlobalUseCase) {
        this.buscarGlobalUseCase = buscarGlobalUseCase;
    }

    @GetMapping
    public ResponseEntity<BuscaGlobalResponse> buscarGlobal(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "5") int limitePorTipo
    ) {
        return ResponseEntity.ok(BuscaGlobalResponse.de(
                buscarGlobalUseCase.buscarGlobal(empresaId, busca, categoria, status, limitePorTipo)
        ));
    }
}
