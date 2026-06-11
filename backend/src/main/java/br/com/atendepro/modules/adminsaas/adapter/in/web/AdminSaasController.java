package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.adminsaas.application.port.in.AlterarBloqueioEmpresaAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarDashboardAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarDashboardVendasAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarAuditoriaOperacionalAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.DetalharEmpresaAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ListarEmpresasAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ObservarEmpresaAdminSaasUseCase;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin-saas")
@Profile("!test")
public class AdminSaasController {

    private final ConsultarAdminSaasUseCase consultarAdminSaasUseCase;
    private final ConsultarDashboardAdminSaasUseCase consultarDashboardAdminSaasUseCase;
    private final ConsultarDashboardVendasAdminSaasUseCase consultarDashboardVendasAdminSaasUseCase;
    private final ConsultarAuditoriaOperacionalAdminSaasUseCase consultarAuditoriaOperacionalAdminSaasUseCase;
    private final ListarEmpresasAdminSaasUseCase listarEmpresasAdminSaasUseCase;
    private final DetalharEmpresaAdminSaasUseCase detalharEmpresaAdminSaasUseCase;
    private final AlterarBloqueioEmpresaAdminSaasUseCase alterarBloqueioEmpresaAdminSaasUseCase;
    private final ObservarEmpresaAdminSaasUseCase observarEmpresaAdminSaasUseCase;

    public AdminSaasController(
            ConsultarAdminSaasUseCase consultarAdminSaasUseCase,
            ConsultarDashboardAdminSaasUseCase consultarDashboardAdminSaasUseCase,
            ConsultarDashboardVendasAdminSaasUseCase consultarDashboardVendasAdminSaasUseCase,
            ConsultarAuditoriaOperacionalAdminSaasUseCase consultarAuditoriaOperacionalAdminSaasUseCase,
            ListarEmpresasAdminSaasUseCase listarEmpresasAdminSaasUseCase,
            DetalharEmpresaAdminSaasUseCase detalharEmpresaAdminSaasUseCase,
            AlterarBloqueioEmpresaAdminSaasUseCase alterarBloqueioEmpresaAdminSaasUseCase,
            ObservarEmpresaAdminSaasUseCase observarEmpresaAdminSaasUseCase
    ) {
        this.consultarAdminSaasUseCase = consultarAdminSaasUseCase;
        this.consultarDashboardAdminSaasUseCase = consultarDashboardAdminSaasUseCase;
        this.consultarDashboardVendasAdminSaasUseCase = consultarDashboardVendasAdminSaasUseCase;
        this.consultarAuditoriaOperacionalAdminSaasUseCase = consultarAuditoriaOperacionalAdminSaasUseCase;
        this.listarEmpresasAdminSaasUseCase = listarEmpresasAdminSaasUseCase;
        this.detalharEmpresaAdminSaasUseCase = detalharEmpresaAdminSaasUseCase;
        this.alterarBloqueioEmpresaAdminSaasUseCase = alterarBloqueioEmpresaAdminSaasUseCase;
        this.observarEmpresaAdminSaasUseCase = observarEmpresaAdminSaasUseCase;
    }

    @GetMapping("/status")
    public ResponseEntity<AdminSaasStatusResponse> consultarStatus() {
        return ResponseEntity.ok(AdminSaasStatusResponse.de(consultarAdminSaasUseCase.consultarStatus()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminSaasDashboardResponse> consultarDashboard() {
        return ResponseEntity.ok(AdminSaasDashboardResponse.de(consultarDashboardAdminSaasUseCase.consultarDashboard()));
    }

    @GetMapping("/dashboard/vendas")
    public ResponseEntity<DashboardVendasAdminSaasResponse> consultarDashboardVendas() {
        return ResponseEntity.ok(DashboardVendasAdminSaasResponse.de(
                consultarDashboardVendasAdminSaasUseCase.consultarDashboardVendas()
        ));
    }


    @GetMapping("/auditoria/operacional")
    public ResponseEntity<AuditoriaOperacionalAdminSaasResponse> consultarAuditoriaOperacional() {
        return ResponseEntity.ok(AuditoriaOperacionalAdminSaasResponse.de(
                consultarAuditoriaOperacionalAdminSaasUseCase.consultarAuditoriaOperacional()
        ));
    }
    @GetMapping("/empresas")
    public ResponseEntity<EmpresasAdminSaasPaginadasResponse> listarEmpresas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(EmpresasAdminSaasPaginadasResponse.de(
                listarEmpresasAdminSaasUseCase.listarEmpresas(new Paginacao(pagina, tamanho), busca)
        ));
    }

    @GetMapping("/empresas/{empresaId}")
    public ResponseEntity<EmpresaAdminSaasDetalheResponse> detalharEmpresa(@PathVariable UUID empresaId) {
        return detalharEmpresaAdminSaasUseCase.detalharEmpresa(empresaId)
                .map(EmpresaAdminSaasDetalheResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/empresas/{empresaId}/bloqueio")
    public ResponseEntity<EmpresaAdminSaasDetalheResponse> alterarBloqueioEmpresa(
            @PathVariable UUID empresaId,
            @Valid @RequestBody AlterarBloqueioEmpresaAdminSaasRequest request
    ) {
        return alterarBloqueioEmpresaAdminSaasUseCase.alterarBloqueioEmpresa(request.paraCommand(empresaId))
                .map(EmpresaAdminSaasDetalheResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/empresas/{empresaId}/observacao")
    public ResponseEntity<EmpresaAdminSaasObservacaoResponse> observarEmpresa(@PathVariable UUID empresaId) {
        return observarEmpresaAdminSaasUseCase.observarEmpresa(empresaId)
                .map(EmpresaAdminSaasObservacaoResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
