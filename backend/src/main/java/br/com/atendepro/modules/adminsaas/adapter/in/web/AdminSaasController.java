package br.com.atendepro.modules.adminsaas.adapter.in.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarDashboardAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarAdminSaasUseCase;

@RestController
@RequestMapping("/api/admin-saas")
@Profile("!test")
public class AdminSaasController {

    private final ConsultarAdminSaasUseCase consultarAdminSaasUseCase;
    private final ConsultarDashboardAdminSaasUseCase consultarDashboardAdminSaasUseCase;

    public AdminSaasController(
            ConsultarAdminSaasUseCase consultarAdminSaasUseCase,
            ConsultarDashboardAdminSaasUseCase consultarDashboardAdminSaasUseCase
    ) {
        this.consultarAdminSaasUseCase = consultarAdminSaasUseCase;
        this.consultarDashboardAdminSaasUseCase = consultarDashboardAdminSaasUseCase;
    }

    @GetMapping("/status")
    public ResponseEntity<AdminSaasStatusResponse> consultarStatus() {
        return ResponseEntity.ok(AdminSaasStatusResponse.de(consultarAdminSaasUseCase.consultarStatus()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminSaasDashboardResponse> consultarDashboard() {
        return ResponseEntity.ok(AdminSaasDashboardResponse.de(consultarDashboardAdminSaasUseCase.consultarDashboard()));
    }
}
