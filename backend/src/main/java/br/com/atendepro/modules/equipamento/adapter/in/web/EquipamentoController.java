package br.com.atendepro.modules.equipamento.adapter.in.web;

import java.net.URI;
import java.time.LocalDate;
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

import br.com.atendepro.modules.equipamento.application.port.in.BuscarEquipamentoUseCase;
import br.com.atendepro.modules.equipamento.application.port.in.CadastrarEquipamentoUseCase;
import br.com.atendepro.modules.equipamento.application.port.in.ListarEquipamentosUseCase;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/equipamentos")
@Profile("!test")
public class EquipamentoController {

    private final CadastrarEquipamentoUseCase cadastrarEquipamentoUseCase;
    private final BuscarEquipamentoUseCase buscarEquipamentoUseCase;
    private final ListarEquipamentosUseCase listarEquipamentosUseCase;

    public EquipamentoController(
            CadastrarEquipamentoUseCase cadastrarEquipamentoUseCase,
            BuscarEquipamentoUseCase buscarEquipamentoUseCase,
            ListarEquipamentosUseCase listarEquipamentosUseCase
    ) {
        this.cadastrarEquipamentoUseCase = cadastrarEquipamentoUseCase;
        this.buscarEquipamentoUseCase = buscarEquipamentoUseCase;
        this.listarEquipamentosUseCase = listarEquipamentosUseCase;
    }

    @PostMapping
    public ResponseEntity<EquipamentoResponse> cadastrarEquipamento(
            @Valid @RequestBody CadastrarEquipamentoRequest request
    ) {
        EquipamentoResponse response = EquipamentoResponse.de(
                cadastrarEquipamentoUseCase.cadastrarEquipamento(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/equipamentos/" + response.id())).body(response);
    }

    @GetMapping("/{equipamentoId}")
    public ResponseEntity<EquipamentoResponse> buscarEquipamento(@PathVariable UUID equipamentoId) {
        return buscarEquipamentoUseCase.buscarEquipamentoPorId(equipamentoId)
                .map(EquipamentoResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<EquipamentosPaginadosResponse> listarEquipamentos(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) LocalDate manutencaoAte
    ) {
        return ResponseEntity.ok(EquipamentosPaginadosResponse.de(
                listarEquipamentosUseCase.listarEquipamentos(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        categoria,
                        ativo,
                        manutencaoAte
                )
        ));
    }
}
