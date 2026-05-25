package br.com.atendepro.modules.suporte.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.suporte.application.port.in.AbrirChamadoSuporteUseCase;
import br.com.atendepro.modules.suporte.application.port.in.AtualizarTriagemChamadoSuporteUseCase;
import br.com.atendepro.modules.suporte.application.port.in.DetalharChamadoSuporteUseCase;
import br.com.atendepro.modules.suporte.application.port.in.ListarChamadosSuporteUseCase;
import br.com.atendepro.modules.suporte.application.port.in.RegistrarMensagemChamadoSuporteUseCase;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/suporte/chamados")
@Profile("!test")
public class ChamadoSuporteController {

    private final AbrirChamadoSuporteUseCase abrirChamadoSuporteUseCase;
    private final AtualizarTriagemChamadoSuporteUseCase atualizarTriagemChamadoSuporteUseCase;
    private final DetalharChamadoSuporteUseCase detalharChamadoSuporteUseCase;
    private final ListarChamadosSuporteUseCase listarChamadosSuporteUseCase;
    private final RegistrarMensagemChamadoSuporteUseCase registrarMensagemChamadoSuporteUseCase;

    public ChamadoSuporteController(
            AbrirChamadoSuporteUseCase abrirChamadoSuporteUseCase,
            AtualizarTriagemChamadoSuporteUseCase atualizarTriagemChamadoSuporteUseCase,
            DetalharChamadoSuporteUseCase detalharChamadoSuporteUseCase,
            ListarChamadosSuporteUseCase listarChamadosSuporteUseCase,
            RegistrarMensagemChamadoSuporteUseCase registrarMensagemChamadoSuporteUseCase
    ) {
        this.abrirChamadoSuporteUseCase = abrirChamadoSuporteUseCase;
        this.atualizarTriagemChamadoSuporteUseCase = atualizarTriagemChamadoSuporteUseCase;
        this.detalharChamadoSuporteUseCase = detalharChamadoSuporteUseCase;
        this.listarChamadosSuporteUseCase = listarChamadosSuporteUseCase;
        this.registrarMensagemChamadoSuporteUseCase = registrarMensagemChamadoSuporteUseCase;
    }

    @PostMapping
    public ResponseEntity<DetalheChamadoSuporteResponse> abrirChamado(
            @Valid @RequestBody AbrirChamadoSuporteRequest request
    ) {
        DetalheChamadoSuporteResponse response = DetalheChamadoSuporteResponse.de(
                abrirChamadoSuporteUseCase.abrirChamado(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/suporte/chamados/" + response.chamado().id())).body(response);
    }

    @GetMapping("/{chamadoId}")
    public ResponseEntity<DetalheChamadoSuporteResponse> detalharChamado(@PathVariable UUID chamadoId) {
        return detalharChamadoSuporteUseCase.detalharChamado(chamadoId)
                .map(DetalheChamadoSuporteResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ChamadosSuportePaginadosResponse> listarChamados(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) StatusChamadoSuporte status,
            @RequestParam(required = false) PrioridadeChamadoSuporte prioridade
    ) {
        return ResponseEntity.ok(ChamadosSuportePaginadosResponse.de(
                listarChamadosSuporteUseCase.listarChamados(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        status,
                        prioridade
                )
        ));
    }

    @PostMapping("/{chamadoId}/mensagens")
    public ResponseEntity<DetalheChamadoSuporteResponse> registrarMensagem(
            @PathVariable UUID chamadoId,
            @Valid @RequestBody RegistrarMensagemChamadoSuporteRequest request
    ) {
        return ResponseEntity.ok(DetalheChamadoSuporteResponse.de(
                registrarMensagemChamadoSuporteUseCase.registrarMensagem(request.paraCommand(chamadoId))
        ));
    }

    @PatchMapping("/{chamadoId}/triagem")
    public ResponseEntity<DetalheChamadoSuporteResponse> atualizarTriagem(
            @PathVariable UUID chamadoId,
            @RequestBody AtualizarTriagemChamadoSuporteRequest request
    ) {
        return ResponseEntity.ok(DetalheChamadoSuporteResponse.de(
                atualizarTriagemChamadoSuporteUseCase.atualizarTriagem(request.paraCommand(chamadoId))
        ));
    }
}
