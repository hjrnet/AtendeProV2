package br.com.atendepro.modules.adminsaas.adapter.in.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.adminsaas.application.port.in.ResetarDemoAdminSaasUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin-saas/demo")
@Profile("local")
public class AdminSaasDemoController {

    private final ResetarDemoAdminSaasUseCase resetarDemoAdminSaasUseCase;

    public AdminSaasDemoController(ResetarDemoAdminSaasUseCase resetarDemoAdminSaasUseCase) {
        this.resetarDemoAdminSaasUseCase = resetarDemoAdminSaasUseCase;
    }

    @PostMapping("/reset")
    public ResponseEntity<ResetDemoAdminSaasResponse> resetarDemo(
            @Valid @RequestBody ResetDemoAdminSaasRequest request
    ) {
        return ResponseEntity.ok(ResetDemoAdminSaasResponse.de(
                resetarDemoAdminSaasUseCase.resetarDemo(request.paraCommand())
        ));
    }
}
