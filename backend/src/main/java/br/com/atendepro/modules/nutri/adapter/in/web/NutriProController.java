package br.com.atendepro.modules.nutri.adapter.in.web;

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

import br.com.atendepro.modules.nutri.application.command.DetalharAvaliacaoAntropometricaNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarAvaliacoesAntropometricasNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarProntuarioNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarVisaoNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarPacientesNutriProCommand;
import br.com.atendepro.modules.nutri.application.port.in.CriarAvaliacaoAntropometricaNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.DetalharAvaliacaoAntropometricaNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarProntuarioNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarVisaoNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarAvaliacoesAntropometricasNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarPacientesNutriProUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/nutri-pro")
@Profile("!test")
public class NutriProController {

    private final ConsultarVisaoNutriProUseCase consultarVisaoNutriProUseCase;
    private final ListarPacientesNutriProUseCase listarPacientesNutriProUseCase;
    private final ConsultarProntuarioNutriProUseCase consultarProntuarioNutriProUseCase;
    private final CriarAvaliacaoAntropometricaNutriProUseCase criarAvaliacaoAntropometricaNutriProUseCase;
    private final ListarAvaliacoesAntropometricasNutriProUseCase listarAvaliacoesAntropometricasNutriProUseCase;
    private final DetalharAvaliacaoAntropometricaNutriProUseCase detalharAvaliacaoAntropometricaNutriProUseCase;

    public NutriProController(
            ConsultarVisaoNutriProUseCase consultarVisaoNutriProUseCase,
            ListarPacientesNutriProUseCase listarPacientesNutriProUseCase,
            ConsultarProntuarioNutriProUseCase consultarProntuarioNutriProUseCase,
            CriarAvaliacaoAntropometricaNutriProUseCase criarAvaliacaoAntropometricaNutriProUseCase,
            ListarAvaliacoesAntropometricasNutriProUseCase listarAvaliacoesAntropometricasNutriProUseCase,
            DetalharAvaliacaoAntropometricaNutriProUseCase detalharAvaliacaoAntropometricaNutriProUseCase
    ) {
        this.consultarVisaoNutriProUseCase = consultarVisaoNutriProUseCase;
        this.listarPacientesNutriProUseCase = listarPacientesNutriProUseCase;
        this.consultarProntuarioNutriProUseCase = consultarProntuarioNutriProUseCase;
        this.criarAvaliacaoAntropometricaNutriProUseCase = criarAvaliacaoAntropometricaNutriProUseCase;
        this.listarAvaliacoesAntropometricasNutriProUseCase = listarAvaliacoesAntropometricasNutriProUseCase;
        this.detalharAvaliacaoAntropometricaNutriProUseCase = detalharAvaliacaoAntropometricaNutriProUseCase;
    }

    @GetMapping("/visao")
    public ResponseEntity<VisaoNutriProResponse> consultarVisaoNutriPro(
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(VisaoNutriProResponse.de(
                consultarVisaoNutriProUseCase.consultarVisaoNutriPro(new ConsultarVisaoNutriProCommand(empresaId))
        ));
    }

    @GetMapping("/pacientes")
    public ResponseEntity<PacientesNutriProResponse> listarPacientesNutriPro(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(PacientesNutriProResponse.de(
                listarPacientesNutriProUseCase.listarPacientesNutriPro(new ListarPacientesNutriProCommand(empresaId, busca))
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/prontuario")
    public ResponseEntity<ProntuarioNutriProResponse> consultarProntuarioNutriPro(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return consultarProntuarioNutriProUseCase
                .consultarProntuarioNutriPro(new ConsultarProntuarioNutriProCommand(empresaId, pacienteId))
                .map(ProntuarioNutriProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/pacientes/{pacienteId}/avaliacoes-antropometricas")
    public ResponseEntity<AvaliacaoAntropometricaNutriProResponse> criarAvaliacaoAntropometrica(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId,
            @Valid @RequestBody CriarAvaliacaoAntropometricaNutriProRequest request
    ) {
        AvaliacaoAntropometricaNutriProResponse response = AvaliacaoAntropometricaNutriProResponse.de(
                criarAvaliacaoAntropometricaNutriProUseCase.criarAvaliacaoAntropometrica(request.paraCommand(empresaId, pacienteId))
        );
        return ResponseEntity.created(URI.create("/api/nutri-pro/pacientes/" + pacienteId + "/avaliacoes-antropometricas/" + response.id()))
                .body(response);
    }

    @GetMapping("/pacientes/{pacienteId}/avaliacoes-antropometricas")
    public ResponseEntity<AvaliacoesAntropometricasNutriProResponse> listarAvaliacoesAntropometricas(
            @PathVariable UUID pacienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(AvaliacoesAntropometricasNutriProResponse.de(
                listarAvaliacoesAntropometricasNutriProUseCase.listarAvaliacoesAntropometricas(
                        new ListarAvaliacoesAntropometricasNutriProCommand(empresaId, pacienteId)
                )
        ));
    }

    @GetMapping("/pacientes/{pacienteId}/avaliacoes-antropometricas/{avaliacaoId}")
    public ResponseEntity<AvaliacaoAntropometricaNutriProResponse> detalharAvaliacaoAntropometrica(
            @PathVariable UUID pacienteId,
            @PathVariable UUID avaliacaoId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return detalharAvaliacaoAntropometricaNutriProUseCase
                .detalharAvaliacaoAntropometrica(new DetalharAvaliacaoAntropometricaNutriProCommand(empresaId, pacienteId, avaliacaoId))
                .map(AvaliacaoAntropometricaNutriProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
