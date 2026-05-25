package br.com.atendepro.modules.nutri.adapter.in.web;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.nutri.application.command.ConsultarProntuarioNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ConsultarVisaoNutriProCommand;
import br.com.atendepro.modules.nutri.application.command.ListarPacientesNutriProCommand;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarProntuarioNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarVisaoNutriProUseCase;
import br.com.atendepro.modules.nutri.application.port.in.ListarPacientesNutriProUseCase;

@RestController
@RequestMapping("/api/nutri-pro")
@Profile("!test")
public class NutriProController {

    private final ConsultarVisaoNutriProUseCase consultarVisaoNutriProUseCase;
    private final ListarPacientesNutriProUseCase listarPacientesNutriProUseCase;
    private final ConsultarProntuarioNutriProUseCase consultarProntuarioNutriProUseCase;

    public NutriProController(
            ConsultarVisaoNutriProUseCase consultarVisaoNutriProUseCase,
            ListarPacientesNutriProUseCase listarPacientesNutriProUseCase,
            ConsultarProntuarioNutriProUseCase consultarProntuarioNutriProUseCase
    ) {
        this.consultarVisaoNutriProUseCase = consultarVisaoNutriProUseCase;
        this.listarPacientesNutriProUseCase = listarPacientesNutriProUseCase;
        this.consultarProntuarioNutriProUseCase = consultarProntuarioNutriProUseCase;
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
}
