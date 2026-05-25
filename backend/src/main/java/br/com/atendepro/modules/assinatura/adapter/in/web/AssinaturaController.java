package br.com.atendepro.modules.assinatura.adapter.in.web;

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

import br.com.atendepro.modules.assinatura.application.port.in.AlterarPlanoAssinaturaUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.BuscarAssinaturaUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.CriarAssinaturaUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.GerenciarStatusAssinaturaUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.ListarAssinaturasUseCase;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaStatus;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin-saas/assinaturas")
@Profile("!test")
public class AssinaturaController {

    private final CriarAssinaturaUseCase criarAssinaturaUseCase;
    private final BuscarAssinaturaUseCase buscarAssinaturaUseCase;
    private final ListarAssinaturasUseCase listarAssinaturasUseCase;
    private final AlterarPlanoAssinaturaUseCase alterarPlanoAssinaturaUseCase;
    private final GerenciarStatusAssinaturaUseCase gerenciarStatusAssinaturaUseCase;

    public AssinaturaController(
            CriarAssinaturaUseCase criarAssinaturaUseCase,
            BuscarAssinaturaUseCase buscarAssinaturaUseCase,
            ListarAssinaturasUseCase listarAssinaturasUseCase,
            AlterarPlanoAssinaturaUseCase alterarPlanoAssinaturaUseCase,
            GerenciarStatusAssinaturaUseCase gerenciarStatusAssinaturaUseCase
    ) {
        this.criarAssinaturaUseCase = criarAssinaturaUseCase;
        this.buscarAssinaturaUseCase = buscarAssinaturaUseCase;
        this.listarAssinaturasUseCase = listarAssinaturasUseCase;
        this.alterarPlanoAssinaturaUseCase = alterarPlanoAssinaturaUseCase;
        this.gerenciarStatusAssinaturaUseCase = gerenciarStatusAssinaturaUseCase;
    }

    @PostMapping
    public ResponseEntity<AssinaturaResponse> criarAssinatura(@Valid @RequestBody CriarAssinaturaRequest request) {
        AssinaturaResponse response = AssinaturaResponse.de(criarAssinaturaUseCase.criarAssinatura(request.paraCommand()));
        return ResponseEntity.created(URI.create("/api/admin-saas/assinaturas/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<AssinaturasPaginadasResponse> listarAssinaturas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) AssinaturaStatus status
    ) {
        return ResponseEntity.ok(AssinaturasPaginadasResponse.de(
                listarAssinaturasUseCase.listarAssinaturas(new Paginacao(pagina, tamanho), status)
        ));
    }

    @GetMapping("/{assinaturaId}")
    public ResponseEntity<AssinaturaResponse> buscarAssinatura(@PathVariable UUID assinaturaId) {
        return buscarAssinaturaUseCase.buscarAssinaturaPorId(assinaturaId)
                .map(AssinaturaResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{assinaturaId}/plano")
    public ResponseEntity<AssinaturaResponse> alterarPlano(
            @PathVariable UUID assinaturaId,
            @Valid @RequestBody AlterarPlanoAssinaturaRequest request
    ) {
        return alterarPlanoAssinaturaUseCase.alterarPlanoAssinatura(request.paraCommand(assinaturaId))
                .map(AssinaturaResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{assinaturaId}/cancelar")
    public ResponseEntity<AssinaturaResponse> cancelarAssinatura(@PathVariable UUID assinaturaId) {
        return gerenciarStatusAssinaturaUseCase.cancelarAssinatura(assinaturaId)
                .map(AssinaturaResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{assinaturaId}/bloquear")
    public ResponseEntity<AssinaturaResponse> bloquearAssinatura(@PathVariable UUID assinaturaId) {
        return gerenciarStatusAssinaturaUseCase.bloquearAssinatura(assinaturaId)
                .map(AssinaturaResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{assinaturaId}/desbloquear")
    public ResponseEntity<AssinaturaResponse> desbloquearAssinatura(@PathVariable UUID assinaturaId) {
        return gerenciarStatusAssinaturaUseCase.desbloquearAssinatura(assinaturaId)
                .map(AssinaturaResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
