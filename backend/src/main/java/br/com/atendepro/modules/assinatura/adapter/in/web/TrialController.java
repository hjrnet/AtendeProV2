package br.com.atendepro.modules.assinatura.adapter.in.web;

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

import br.com.atendepro.modules.assinatura.application.port.in.BuscarTrialUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.ConverterTrialUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.IniciarTrialUseCase;
import br.com.atendepro.modules.assinatura.application.port.in.ListarTrialsUseCase;
import br.com.atendepro.modules.assinatura.domain.model.TrialStatus;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin-saas/trials")
@Profile("!test")
public class TrialController {

    private final IniciarTrialUseCase iniciarTrialUseCase;
    private final BuscarTrialUseCase buscarTrialUseCase;
    private final ConverterTrialUseCase converterTrialUseCase;
    private final ListarTrialsUseCase listarTrialsUseCase;

    public TrialController(
            IniciarTrialUseCase iniciarTrialUseCase,
            BuscarTrialUseCase buscarTrialUseCase,
            ConverterTrialUseCase converterTrialUseCase,
            ListarTrialsUseCase listarTrialsUseCase
    ) {
        this.iniciarTrialUseCase = iniciarTrialUseCase;
        this.buscarTrialUseCase = buscarTrialUseCase;
        this.converterTrialUseCase = converterTrialUseCase;
        this.listarTrialsUseCase = listarTrialsUseCase;
    }

    @PostMapping
    public ResponseEntity<TrialResponse> iniciarTrial(@Valid @RequestBody IniciarTrialRequest request) {
        TrialResponse response = TrialResponse.de(iniciarTrialUseCase.iniciarTrial(request.paraCommand()));
        return ResponseEntity.created(URI.create("/api/admin-saas/trials/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<TrialsPaginadosResponse> listarTrials(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) TrialStatus status
    ) {
        return ResponseEntity.ok(TrialsPaginadosResponse.de(
                listarTrialsUseCase.listarTrials(new Paginacao(pagina, tamanho), status)
        ));
    }

    @GetMapping("/{trialId}")
    public ResponseEntity<TrialResponse> buscarTrial(@PathVariable UUID trialId) {
        return buscarTrialUseCase.buscarTrialPorId(trialId)
                .map(TrialResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{trialId}/converter")
    public ResponseEntity<TrialResponse> converterTrial(@PathVariable UUID trialId) {
        return converterTrialUseCase.converterTrial(trialId)
                .map(TrialResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
