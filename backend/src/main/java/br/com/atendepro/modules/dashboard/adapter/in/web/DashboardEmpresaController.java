package br.com.atendepro.modules.dashboard.adapter.in.web;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.dashboard.application.port.in.ConsultarDashboardEmpresaUseCase;

@RestController
@RequestMapping("/api/dashboard/empresa")
@Profile("!test")
public class DashboardEmpresaController {

    private final ConsultarDashboardEmpresaUseCase consultarDashboardEmpresaUseCase;

    public DashboardEmpresaController(ConsultarDashboardEmpresaUseCase consultarDashboardEmpresaUseCase) {
        this.consultarDashboardEmpresaUseCase = consultarDashboardEmpresaUseCase;
    }

    @GetMapping
    public ResponseEntity<DashboardEmpresaResponse> consultarDashboardEmpresa(
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(DashboardEmpresaResponse.de(
                consultarDashboardEmpresaUseCase.consultarDashboardEmpresa(empresaId)
        ));
    }
}
