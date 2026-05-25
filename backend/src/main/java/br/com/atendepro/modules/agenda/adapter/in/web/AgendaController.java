package br.com.atendepro.modules.agenda.adapter.in.web;

import java.net.URI;
import java.time.Instant;
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

import br.com.atendepro.modules.agenda.application.port.in.AgendarCompromissoUseCase;
import br.com.atendepro.modules.agenda.application.port.in.BuscarCompromissoAgendaUseCase;
import br.com.atendepro.modules.agenda.application.port.in.ListarAgendaUseCase;
import br.com.atendepro.modules.agenda.domain.model.AgendaStatus;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agenda/compromissos")
@Profile("!test")
public class AgendaController {

    private final AgendarCompromissoUseCase agendarCompromissoUseCase;
    private final BuscarCompromissoAgendaUseCase buscarCompromissoAgendaUseCase;
    private final ListarAgendaUseCase listarAgendaUseCase;

    public AgendaController(
            AgendarCompromissoUseCase agendarCompromissoUseCase,
            BuscarCompromissoAgendaUseCase buscarCompromissoAgendaUseCase,
            ListarAgendaUseCase listarAgendaUseCase
    ) {
        this.agendarCompromissoUseCase = agendarCompromissoUseCase;
        this.buscarCompromissoAgendaUseCase = buscarCompromissoAgendaUseCase;
        this.listarAgendaUseCase = listarAgendaUseCase;
    }

    @PostMapping
    public ResponseEntity<CompromissoAgendaResponse> agendarCompromisso(
            @Valid @RequestBody AgendarCompromissoRequest request
    ) {
        CompromissoAgendaResponse response = CompromissoAgendaResponse.de(
                agendarCompromissoUseCase.agendarCompromisso(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/agenda/compromissos/" + response.id())).body(response);
    }

    @GetMapping("/{compromissoId}")
    public ResponseEntity<CompromissoAgendaResponse> buscarCompromisso(@PathVariable UUID compromissoId) {
        return buscarCompromissoAgendaUseCase.buscarCompromissoPorId(compromissoId)
                .map(CompromissoAgendaResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<AgendaPaginadaResponse> listarAgenda(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) Instant inicio,
            @RequestParam(required = false) Instant fim,
            @RequestParam(required = false) UUID profissionalId,
            @RequestParam(required = false) String sala,
            @RequestParam(required = false) AgendaStatus status
    ) {
        return ResponseEntity.ok(AgendaPaginadaResponse.de(
                listarAgendaUseCase.listarAgenda(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        inicio,
                        fim,
                        profissionalId,
                        sala,
                        status
                )
        ));
    }
}
